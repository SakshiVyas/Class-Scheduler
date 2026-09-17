package com.campusscheduler.algorithm;

import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.TimeSlot;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BacktrackingSolverTest {
    @Test void returnsBestEffortWhenCourseCannotFit() {
        Course possible = SchedulerTestData.course("C1", 20, "P1");
        Course impossible = SchedulerTestData.course("C2", 1000, "P2");
        ScheduleResult result = new BacktrackingSolver().solve(SchedulerTestData.data(
                List.of(possible, impossible), List.of(new Room("R1", 30)), List.of(SchedulerTestData.slot("MON_09", "Monday", "09:00")), Map.of()));
        assertEquals(1, result.getScheduledEntries().size());
        assertEquals(List.of(impossible), result.getUnscheduledCourses());
    }

    @Test void choosesLowerWasteWhenCourseCountTies() {
        Course course = SchedulerTestData.course("C1", 25, "P1");
        ScheduleResult result = new BacktrackingSolver().solve(SchedulerTestData.data(
                List.of(course), List.of(new Room("SMALL", 30), new Room("LARGE", 100)), List.of(SchedulerTestData.slot("MON_09", "Monday", "09:00")), Map.of()));
        assertEquals("SMALL", result.getScheduledEntries().get(0).getRoom().getId());
        assertEquals(5, result.getTotalWastedSeats());
    }

    @Test void prioritisesLargerClassesWhenConflictDegreeIsEqual() {
        Course small = SchedulerTestData.course("SMALL", 20, "P1");
        Course large = SchedulerTestData.course("LARGE", 80, "P2");
        var data = SchedulerTestData.data(List.of(small, large), List.of(new Room("R1", 100)),
                List.of(SchedulerTestData.slot("MON_09", "Monday", "09:00")), Map.of());
        assertEquals("LARGE", new BacktrackingSolver().sortCoursesByDifficulty(data).get(0).getId());
    }
}
