package com.campusscheduler.algorithm;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.TimeSlot;
import com.campusscheduler.service.TimeSlotGenerator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GreedySolver {
    public List<Course> sortCoursesBySize(List<Course> courses) {
        List<Course> sorted = new ArrayList<>(courses);
        sorted.sort(Comparator.comparingInt(Course::getEnrolledStudents).reversed());
        return sorted;
    }

    private boolean sharesGroup(Course first, Course second, Map<String, List<String>> groups) {
        if (groups == null) return false;
        for (List<String> courses : groups.values()) {
            if (courses != null && courses.contains(first.getId()) && courses.contains(second.getId())) return true;
        }
        return false;
    }

    private boolean valid(Course course, Room room, TimeSlot slot, List<ScheduleEntry> schedule, Map<String, List<String>> groups) {
        if (room.getCapacity() < course.getEnrolledStudents()) return false;
        for (ScheduleEntry entry : schedule) {
            if (!entry.getTimeSlot().getId().equals(slot.getId())) continue;
            if (entry.getRoom().getId().equals(room.getId())) return false;
            if (entry.getCourse().getProfessorId().equals(course.getProfessorId())) return false;
            if (sharesGroup(entry.getCourse(), course, groups)) return false;
        }
        return true;
    }

    public ScheduleResult solve(ConstraintData data) {
        ScheduleResult result = new ScheduleResult();
        for (Course course : sortCoursesBySize(data.getClasses())) {
            boolean assigned = false;
            for (TimeSlot slot : new TimeSlotGenerator().generate(data.getScheduleSettings())) {
                List<Room> rooms = new ArrayList<>(data.getRooms());
                rooms.sort(Comparator.comparingInt(Room::getCapacity));
                for (Room room : rooms) {
                    if (valid(course, room, slot, result.getScheduledEntries(), data.getStudentGroups())) {
                        result.addScheduledEntry(new ScheduleEntry(course, room, slot, room.getCapacity() - course.getEnrolledStudents()));
                        assigned = true;
                        break;
                    }
                }
                if (assigned) break;
            }
            if (!assigned) result.addUnscheduledCourse(course);
        }
        return result;
    }
}
