package com.campusscheduler.algorithm;

import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.TimeSlot;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicProgrammingOptimizerTest {
    @Test void minimisesWasteForFixedTimeslot() {
        List<Course> courses = List.of(SchedulerTestData.course("C1", 30, "P1"), SchedulerTestData.course("C2", 50, "P2"));
        List<Room> rooms = List.of(new Room("R1", 30), new Room("R2", 50), new Room("R3", 100));
        List<ScheduleEntry> entries = new DynamicProgrammingOptimizer().optimizeRoomsForTimeSlot(courses, rooms, SchedulerTestData.slot("MON_09", "Monday", "09:00"));
        assertEquals(2, entries.size());
        assertEquals(0, entries.stream().mapToInt(ScheduleEntry::getWastedSeats).sum());
    }

    @Test void returnsNoAssignmentsWhenTooFewRooms() {
        List<Course> courses = List.of(SchedulerTestData.course("C1", 20, "P1"), SchedulerTestData.course("C2", 20, "P2"), SchedulerTestData.course("C3", 20, "P3"));
        List<ScheduleEntry> entries = new DynamicProgrammingOptimizer().optimizeRoomsForTimeSlot(courses, List.of(new Room("R1", 30), new Room("R2", 30)), SchedulerTestData.slot("MON_09", "Monday", "09:00"));
        assertEquals(0, entries.size());
    }
}
