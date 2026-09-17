package com.campusscheduler.service;

import com.campusscheduler.algorithm.BacktrackingSolver;
import com.campusscheduler.algorithm.ConflictGraph;
import com.campusscheduler.algorithm.DynamicProgrammingOptimizer;
import com.campusscheduler.algorithm.GreedySolver;
import com.campusscheduler.algorithm.ScheduleValidator;
import com.campusscheduler.algorithm.WelshPowellSolver;
import com.campusscheduler.api.dto.AlgorithmRun;
import com.campusscheduler.api.dto.ConflictReportEntry;
import com.campusscheduler.api.dto.ScheduleComparison;
import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.TimeSlot;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {
    private final DatabaseConstraintService constraints;
    private final BacktrackingSolver backtracking;
    private final GreedySolver greedy;
    private final ConsoleScheduleReporter consoleReporter;

    public SchedulingService(DatabaseConstraintService constraints, BacktrackingSolver backtracking, GreedySolver greedy,
                             ConsoleScheduleReporter consoleReporter) {
        this.constraints = constraints;
        this.backtracking = backtracking;
        this.greedy = greedy;
        this.consoleReporter = consoleReporter;
    }

    public ScheduleResult generate(String algorithm) {
        var data = constraints.loadConstraints();
        if ("greedy".equalsIgnoreCase(algorithm)) return greedy.solve(data);
        if (algorithm == null || "backtracking".equalsIgnoreCase(algorithm)) return backtracking.solve(data);
        if ("dynamic-programming".equalsIgnoreCase(algorithm) || "dp".equalsIgnoreCase(algorithm)) {
            ConflictGraph graph = new ConflictGraph();
            graph.buildGraph(data);
            Map<String, TimeSlot> assignments = new WelshPowellSolver()
                    .assignTimeSlots(graph, slots(data), data.getRooms().size());
            return new DynamicProgrammingOptimizer().optimizeSchedule(data, assignments);
        }
        throw new IllegalArgumentException("Unsupported algorithm: " + algorithm
                + ". Use greedy, dynamic-programming, or backtracking.");
    }

    public ScheduleComparison compare() {
        ConstraintData data = constraints.loadConstraints();
        List<TimeSlot> timeSlots = slots(data);
        List<AlgorithmRun> runs = new ArrayList<>();

        long started = System.nanoTime();
        ScheduleResult greedyResult = greedy.solve(data);
        runs.add(scheduleRun("Greedy", started, data, greedyResult, "Complete"));

        started = System.nanoTime();
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(data);
        Map<String, TimeSlot> coloring = new WelshPowellSolver()
                .assignTimeSlots(graph, timeSlots, data.getRooms().size());
        runs.add(coloringRun(started, data, coloring));

        started = System.nanoTime();
        ScheduleResult dpResult = new DynamicProgrammingOptimizer().optimizeSchedule(data, coloring);
        runs.add(scheduleRun("Dynamic Programming", started, data, dpResult, "Complete"));

        started = System.nanoTime();
        ScheduleResult backtrackingResult;
        boolean limited;
        synchronized (backtracking) {
            backtrackingResult = backtracking.solve(data);
            limited = backtracking.wasSearchLimitReached();
        }
        String backtrackingStatus;
        if (!backtrackingResult.getUnscheduledCourses().isEmpty()) {
            backtrackingStatus = limited ? "Search limit reached; partial schedule returned" : "Best feasible schedule";
        } else if (limited) {
            backtrackingStatus = "Complete schedule; optimization limit reached";
        } else {
            backtrackingStatus = "Complete and optimal";
        }
        runs.add(scheduleRun("Backtracking", started, data, backtrackingResult, backtrackingStatus));

        ScheduleComparison comparison = new ScheduleComparison(data.getClasses().size(), data.getRooms().size(), timeSlots.size(), runs);
        consoleReporter.print(comparison);
        return comparison;
    }

    private AlgorithmRun scheduleRun(String algorithm, long started, ConstraintData data, ScheduleResult result, String status) {
        return new AlgorithmRun(
                algorithm,
                elapsedMillis(started),
                result.getScheduledEntries().size(),
                result.getUnscheduledCourses().size(),
                result.getTotalWastedSeats(),
                status,
                result.getScheduledEntries(),
                Map.of(),
                conflictReport(data, result)
        );
    }

    private AlgorithmRun coloringRun(long started, ConstraintData data, Map<String, TimeSlot> assignments) {
        List<ConflictReportEntry> report = data.getClasses().stream()
                .filter(course -> !assignments.containsKey(course.getId()))
                .map(course -> new ConflictReportEntry(course.getId(), "The conflict graph requires more colors than the available time slots."))
                .toList();
        return new AlgorithmRun(
                "Welsh-Powell",
                elapsedMillis(started),
                assignments.size(),
                data.getClasses().size() - assignments.size(),
                null,
                "Complete",
                List.of(),
                new LinkedHashMap<>(assignments),
                report
        );
    }

    private List<ConflictReportEntry> conflictReport(ConstraintData data, ScheduleResult result) {
        return result.getUnscheduledCourses().stream()
                .map(course -> new ConflictReportEntry(course.getId(), explain(course, data, result.getScheduledEntries())))
                .toList();
    }

    private String explain(Course course, ConstraintData data, List<ScheduleEntry> schedule) {
        boolean roomFits = data.getRooms().stream().anyMatch(room -> room.getCapacity() >= course.getEnrolledStudents());
        if (!roomFits) return "No room has enough capacity for " + course.getEnrolledStudents() + " students.";
        LinkedHashSet<String> blockers = new LinkedHashSet<>();
        List<TimeSlot> availableSlots = slots(data);
        if (availableSlots.isEmpty()) return "No time slots are configured.";
        for (TimeSlot slot : availableSlots) {
            List<ScheduleEntry> atSlot = schedule.stream()
                    .filter(entry -> entry.getTimeSlot().getId().equals(slot.getId())).toList();
            boolean professorBlocked = atSlot.stream()
                    .anyMatch(entry -> entry.getCourse().getProfessorId().equals(course.getProfessorId()));
            boolean groupBlocked = atSlot.stream().anyMatch(entry -> sharesGroup(course, entry.getCourse(), data));
            boolean roomBlocked = data.getRooms().stream()
                    .filter(room -> room.getCapacity() >= course.getEnrolledStudents())
                    .allMatch(room -> atSlot.stream().anyMatch(entry -> entry.getRoom().getId().equals(room.getId())));
            if (!professorBlocked && !groupBlocked && !roomBlocked) {
                return "A valid placement exists but was not reached before the search limit.";
            }
            if (professorBlocked) blockers.add("professor availability");
            if (groupBlocked) blockers.add("student-group overlap");
            if (roomBlocked) blockers.add("room availability");
        }
        return "All time slots are blocked by " + String.join(", ", blockers) + ".";
    }

    private boolean sharesGroup(Course first, Course second, ConstraintData data) {
        return ScheduleValidator.shareStudentGroup(first, second, data.getStudentGroups());
    }

    private List<TimeSlot> slots(ConstraintData data) {
        return new TimeSlotGenerator().generate(data.getScheduleSettings());
    }

    private long elapsedMillis(long started) {
        return Math.max(0, (System.nanoTime() - started) / 1_000_000L);
    }
}
