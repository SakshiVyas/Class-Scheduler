package com.campusscheduler.algorithm;

import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.TimeSlot;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreedySolverTest {
    @Test void sortsLargestCourseFirst() {
        Course small = SchedulerTestData.course("SMALL", 20, "P1");
        Course large = SchedulerTestData.course("LARGE", 80, "P2");
        List<Course> sorted = new GreedySolver().sortCoursesBySize(List.of(small, large));
        assertEquals("LARGE", sorted.get(0).getId());
    }

    @Test void leavesCourseUnscheduledWhenNoRoomFits() {
        Course course = SchedulerTestData.course("IMPOSSIBLE", 1000, "P1");
        TimeSlot slot = SchedulerTestData.slot("MON_09", "Monday", "09:00");
        ScheduleResult result = new GreedySolver().solve(SchedulerTestData.data(
                List.of(course), List.of(new Room("R1", 100)), List.of(slot), Map.of()));
        assertEquals(0, result.getScheduledEntries().size());
        assertEquals(List.of(course), result.getUnscheduledCourses());
    }

    @Test void preventsProfessorAndStudentGroupConflicts() {
        Course first = SchedulerTestData.course("C1", 20, "P1");
        Course sameProfessor = SchedulerTestData.course("C2", 20, "P1");
        Course sameGroup = SchedulerTestData.course("C3", 20, "P3");
        TimeSlot slot = SchedulerTestData.slot("MON_09", "Monday", "09:00");
        Map<String, List<String>> groups = SchedulerTestData.groups("G1", List.of("C1", "C3"));
        ScheduleResult result = new GreedySolver().solve(SchedulerTestData.data(
                List.of(first, sameProfessor, sameGroup), List.of(new Room("R1", 30), new Room("R2", 30)), List.of(slot), groups));
        assertEquals(1, result.getScheduledEntries().size());
        assertEquals(2, result.getUnscheduledCourses().size());
    }
}
