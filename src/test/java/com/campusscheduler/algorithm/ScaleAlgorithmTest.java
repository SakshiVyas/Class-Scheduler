package com.campusscheduler.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.model.TimeSlot;
import com.campusscheduler.service.TimeSlotGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScaleAlgorithmTest {
    @Test
    void dynamicProgrammingAllocatesAllScaleCourses() throws IOException {
        ConstraintData data = scaleData();
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(data);
        List<TimeSlot> slots = new TimeSlotGenerator().generate(data.getScheduleSettings());
        Map<String, TimeSlot> coloring = new WelshPowellSolver()
                .assignTimeSlots(graph, slots, data.getRooms().size());

        ScheduleResult result = new DynamicProgrammingOptimizer().optimizeSchedule(data, coloring);

        assertEquals(360, coloring.size());
        assertEquals(360, result.getScheduledEntries().size());
        assertTrue(result.getUnscheduledCourses().isEmpty());
    }

    @Test
    void backtrackingFindsCompleteScaleScheduleWithinShortBudget() throws IOException {
        ConstraintData data = scaleData();
        String previousLimit = System.getProperty("scheduler.backtracking.maxMillis");
        System.setProperty("scheduler.backtracking.maxMillis", "3000");
        try {
            BacktrackingSolver solver = new BacktrackingSolver();
            ScheduleResult result = solver.solve(data);
            assertEquals(360, result.getScheduledEntries().size());
            assertTrue(result.getUnscheduledCourses().isEmpty());
            assertFalse(solver.wasSearchLimitReached());
        } finally {
            if (previousLimit == null) System.clearProperty("scheduler.backtracking.maxMillis");
            else System.setProperty("scheduler.backtracking.maxMillis", previousLimit);
        }
    }

    private ConstraintData scaleData() throws IOException {
        ConstraintData data = new ConstraintData();
        List<Course> courses = new ArrayList<>();
        try (var lines = Files.lines(Path.of("data/courses_test.csv")).skip(1)) {
            lines.forEach(line -> {
                String[] row = line.split(",", -1);
                courses.add(new Course(row[0], row[1], row[2], Integer.parseInt(row[3])));
            });
        }
        List<Room> rooms = new ArrayList<>();
        try (var lines = Files.lines(Path.of("data/rooms_test.csv")).skip(1)) {
            lines.forEach(line -> {
                String[] row = line.split(",", -1);
                rooms.add(new Room(row[0], Integer.parseInt(row[1])));
            });
        }
        Map<String, LinkedHashSet<String>> groupSets = new LinkedHashMap<>();
        try (var lines = Files.lines(Path.of("data/students_test.csv")).skip(1)) {
            lines.forEach(line -> {
                String[] row = line.split(",", -1);
                LinkedHashSet<String> group = groupSets.computeIfAbsent(row[2], ignored -> new LinkedHashSet<>());
                for (String courseId : row[3].split(";")) group.add(courseId);
            });
        }
        Map<String, List<String>> groups = new LinkedHashMap<>();
        groupSets.forEach((id, group) -> groups.put(id, new ArrayList<>(group)));
        ScheduleSettings settings = new ScheduleSettings();
        settings.setWorkingDays(List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        settings.setDayStartTime("09:00");
        settings.setDayEndTime("18:00");
        settings.setSlotMinutes(180);
        data.setClasses(courses);
        data.setRooms(rooms);
        data.setStudentGroups(groups);
        data.setScheduleSettings(settings);
        return data;
    }
}
