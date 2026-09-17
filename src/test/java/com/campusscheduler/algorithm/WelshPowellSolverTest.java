package com.campusscheduler.algorithm;

import com.campusscheduler.model.Course;
import com.campusscheduler.model.TimeSlot;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WelshPowellSolverTest {
    @Test void assignsDifferentSlotsToConflictingCourses() {
        Course c1 = SchedulerTestData.course("C1", 20, "P1");
        Course c2 = SchedulerTestData.course("C2", 20, "P1");
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(List.of(c1, c2), Map.of());
        List<TimeSlot> slots = List.of(SchedulerTestData.slot("MON_09", "Monday", "09:00"), SchedulerTestData.slot("MON_10", "Monday", "10:00"));
        Map<String, TimeSlot> assignments = new WelshPowellSolver().assignTimeSlots(graph, slots);
        assertNotEquals(assignments.get("C1").getId(), assignments.get("C2").getId());
    }

    @Test void spreadsLargeColourClassesAcrossRoomCapacity() {
        Course c1 = SchedulerTestData.course("C1", 20, "P1");
        Course c2 = SchedulerTestData.course("C2", 20, "P2");
        Course c3 = SchedulerTestData.course("C3", 20, "P3");
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(List.of(c1, c2, c3), Map.of());
        List<TimeSlot> slots = List.of(SchedulerTestData.slot("MON_09", "Monday", "09:00"),
                SchedulerTestData.slot("MON_10", "Monday", "10:00"));

        Map<String, TimeSlot> assignments = new WelshPowellSolver().assignTimeSlots(graph, slots, 2);

        assertEquals(3, assignments.size());
        assertEquals(2, assignments.values().stream().map(TimeSlot::getId).distinct().count());
    }
}
