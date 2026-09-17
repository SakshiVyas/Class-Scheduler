package com.campusscheduler.service;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.persistence.CourseRepository;
import com.campusscheduler.persistence.RoomRepository;
import com.campusscheduler.persistence.ScheduleSettingsRepository;
import com.campusscheduler.persistence.StudentEntity;
import com.campusscheduler.persistence.StudentRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DatabaseConstraintService {
    private final CourseRepository courses;
    private final RoomRepository rooms;
    private final StudentRepository students;
    private final ScheduleSettingsRepository settings;

    public DatabaseConstraintService(CourseRepository courses, RoomRepository rooms, StudentRepository students,
                                     ScheduleSettingsRepository settings) {
        this.courses = courses;
        this.rooms = rooms;
        this.students = students;
        this.settings = settings;
    }

    public ConstraintData loadConstraints() {
        List<StudentEntity> studentRows = students.findAll();
        ConstraintData data = new ConstraintData();
        data.setClasses(courses.findAll().stream()
                .map(course -> new Course(course.getId(), course.getName(), course.getProfessorId(),
                        course.getEnrolledStudents()))
                .toList());
        data.setRooms(rooms.findAll().stream()
                .map(room -> new Room(room.getId(), room.getCapacity()))
                .toList());
        data.setStudentCount(studentRows.size());
        data.setScheduleSettings(loadScheduleSettings());
        Map<String, LinkedHashSet<String>> groupSets = new LinkedHashMap<>();
        for (StudentEntity student : studentRows) {
            if (student.getGroupId() == null) {
                continue;
            }
            LinkedHashSet<String> groupCourses = groupSets.computeIfAbsent(
                    student.getGroupId(), ignored -> new LinkedHashSet<>());
            if (student.getCourseIds() == null || student.getCourseIds().isBlank()) {
                continue;
            }
            for (String courseId : student.getCourseIds().split(";")) {
                if (!courseId.isBlank()) {
                    groupCourses.add(courseId.trim());
                }
            }
        }
        Map<String, List<String>> groups = new LinkedHashMap<>();
        groupSets.forEach((group, groupCourses) -> groups.put(group, new ArrayList<>(groupCourses)));
        data.setStudentGroups(groups);
        return data;
    }

    private ScheduleSettings loadScheduleSettings() {
        return settings.findById(1).map(entity -> {
            ScheduleSettings schedule = new ScheduleSettings();
            schedule.setWorkingDays(Arrays.stream(entity.getWorkingDays().split(";"))
                    .map(String::trim).filter(day -> !day.isEmpty()).toList());
            schedule.setDayStartTime(entity.getDayStartTime());
            schedule.setDayEndTime(entity.getDayEndTime());
            schedule.setSlotMinutes(entity.getSlotMinutes());
            return schedule;
        }).orElseGet(() -> {
            ScheduleSettings schedule = new ScheduleSettings();
            schedule.setWorkingDays(List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
            schedule.setDayStartTime("09:00");
            schedule.setDayEndTime("18:00");
            schedule.setSlotMinutes(180);
            return schedule;
        });
    }
}
