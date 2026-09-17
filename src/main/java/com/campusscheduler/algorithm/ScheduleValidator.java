package com.campusscheduler.algorithm;

import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.TimeSlot;
import java.util.List;
import java.util.Map;

public final class ScheduleValidator {
    private ScheduleValidator() {
    }

    public static boolean canPlace(Course course, Room room, TimeSlot slot,
                                   List<ScheduleEntry> schedule, Map<String, List<String>> studentGroups) {
        if (room.getCapacity() < course.getEnrolledStudents()) {
            return false;
        }
        for (ScheduleEntry entry : schedule) {
            if (!entry.getTimeSlot().getId().equals(slot.getId())) {
                continue;
            }
            if (entry.getRoom().getId().equals(room.getId())
                    || entry.getCourse().getProfessorId().equals(course.getProfessorId())
                    || shareStudentGroup(entry.getCourse(), course, studentGroups)) {
                return false;
            }
        }
        return true;
    }

    public static boolean shareStudentGroup(Course first, Course second,
                                            Map<String, List<String>> studentGroups) {
        return shareStudentGroup(first.getId(), second.getId(), studentGroups);
    }

    public static boolean shareStudentGroup(String firstCourseId, String secondCourseId,
                                            Map<String, List<String>> studentGroups) {
        if (studentGroups == null) {
            return false;
        }
        return studentGroups.values().stream()
                .filter(courses -> courses != null)
                .anyMatch(courses -> courses.contains(firstCourseId) && courses.contains(secondCourseId));
    }
}
