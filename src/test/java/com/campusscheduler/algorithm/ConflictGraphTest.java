package com.campusscheduler.algorithm;

import com.campusscheduler.model.Course;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConflictGraphTest {
    @Test void connectsCoursesWithSameProfessorOrGroup() {
        Course c1 = SchedulerTestData.course("C1", 20, "P1");
        Course c2 = SchedulerTestData.course("C2", 20, "P1");
        Course c3 = SchedulerTestData.course("C3", 20, "P3");
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(List.of(c1, c2, c3), SchedulerTestData.groups("G1", List.of("C1", "C3")));
        assertTrue(graph.getConflicts("C1").contains("C2"));
        assertTrue(graph.getConflicts("C1").contains("C3"));
        assertFalse(graph.getConflicts("C2").contains("C3"));
    }
}
