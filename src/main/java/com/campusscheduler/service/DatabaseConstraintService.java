package com.campusscheduler.service;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.persistence.CourseRepository;
import com.campusscheduler.persistence.RoomRepository;
import com.campusscheduler.persistence.StudentEntity;
import com.campusscheduler.persistence.StudentRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DatabaseConstraintService {
    private final CourseRepository courses;
    private final RoomRepository rooms;
    private final StudentRepository students;

    public DatabaseConstraintService(CourseRepository courses, RoomRepository rooms, StudentRepository students) {
        this.courses=courses; this.rooms=rooms; this.students=students;
    }

    public ConstraintData loadConstraints() {
        ConstraintData data = new ConstraintData();
        data.setClasses(courses.findAll().stream().map(c -> new Course(c.getId(), c.getName(), c.getProfessorId(), c.getEnrolledStudents())).toList());
        data.setRooms(rooms.findAll().stream().map(r -> new Room(r.getId(), r.getCapacity())).toList());
        ScheduleSettings settings = new ScheduleSettings();
        settings.setWorkingDays(List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        settings.setDayStartTime("09:00");
        settings.setDayEndTime("18:00");
        settings.setSlotMinutes(180);
        data.setScheduleSettings(settings);
        Map<String,List<String>> groups = new LinkedHashMap<>();
        for (StudentEntity student : students.findAll()) {
            if (student.getGroupId() != null) {
                List<String> groupCourses = groups.computeIfAbsent(student.getGroupId(), ignored -> new java.util.ArrayList<>());
                if (student.getCourseIds() != null && !student.getCourseIds().isBlank()) {
                    for (String courseId : student.getCourseIds().split(";")) if (!courseId.isBlank() && !groupCourses.contains(courseId.trim())) groupCourses.add(courseId.trim());
                }
            }
        }
        data.setStudentGroups(groups);
        return data;
    }
}
