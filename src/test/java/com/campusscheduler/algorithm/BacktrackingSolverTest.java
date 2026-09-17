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

    @Test void recoversScheduleThatGreedyCannotComplete() {
        Course a1 = SchedulerTestData.course("A1", 60, "P1");
        Course b1 = SchedulerTestData.course("B1", 55, "P2");
        Course a2 = SchedulerTestData.course("A2", 50, "P3");
        Course b2 = SchedulerTestData.course("B2", 45, "P4");
        Course a3 = SchedulerTestData.course("A3", 40, "P5");
        Course b3 = SchedulerTestData.course("B3", 35, "P6");
        var groups = SchedulerTestData.groups(
                "G1", List.of("A1", "B2"), "G2", List.of("A1", "B3"),
                "G3", List.of("A2", "B1"), "G4", List.of("A2", "B3"),
                "G5", List.of("A3", "B1"), "G6", List.of("A3", "B2"));
        var data = SchedulerTestData.data(
                List.of(a1, b1, a2, b2, a3, b3),
                List.of(new Room("R1", 40), new Room("R2", 60), new Room("R3", 80)),
                List.of(SchedulerTestData.slot("MON_09", "Monday", "09:00"),
                        SchedulerTestData.slot("MON_12", "Monday", "12:00")), groups);

        ScheduleResult greedy = new GreedySolver().solve(data);
        ScheduleResult backtracking = new BacktrackingSolver().solve(data);

        assertEquals(4, greedy.getScheduledEntries().size());
        assertEquals(6, backtracking.getScheduledEntries().size());
    }
}
