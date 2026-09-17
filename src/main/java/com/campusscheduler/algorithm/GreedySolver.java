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
import org.springframework.stereotype.Component;

@Component
public class GreedySolver {
    public List<Course> sortCoursesBySize(List<Course> courses) {
        List<Course> sorted = new ArrayList<>(courses);
        sorted.sort(Comparator.comparingInt(Course::getEnrolledStudents).reversed());
        return sorted;
    }

    public ScheduleResult solve(ConstraintData data) {
        ScheduleResult result = new ScheduleResult();
        List<TimeSlot> slots = new TimeSlotGenerator().generate(data.getScheduleSettings());
        List<Room> rooms = new ArrayList<>(data.getRooms());
        rooms.sort(Comparator.comparingInt(Room::getCapacity));
        for (Course course : sortCoursesBySize(data.getClasses())) {
            boolean assigned = false;
            for (TimeSlot slot : slots) {
                for (Room room : rooms) {
                    if (ScheduleValidator.canPlace(course, room, slot, result.getScheduledEntries(),
                            data.getStudentGroups())) {
                        int wastedSeats = room.getCapacity() - course.getEnrolledStudents();
                        result.addScheduledEntry(new ScheduleEntry(course, room, slot, wastedSeats));
                        assigned = true;
                        break;
                    }
                }
                if (assigned) break;
            }
            if (!assigned) {
                result.addUnscheduledCourse(course);
            }
        }
        return result;
    }
}
