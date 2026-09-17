package com.campusscheduler.demo;

import com.campusscheduler.algorithm.BacktrackingSolver;
import com.campusscheduler.algorithm.ConflictGraph;
import com.campusscheduler.algorithm.DynamicProgrammingOptimizer;
import com.campusscheduler.algorithm.GreedySolver;
import com.campusscheduler.algorithm.WelshPowellSolver;
import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.TimeSlot;
import com.campusscheduler.service.TimeSlotGenerator;
import java.util.Map;
import java.util.List;

public class AlgorithmDemo {
    private void printResult(String title, ScheduleResult result) {
        System.out.println("\n===== " + title + " =====");
        for (ScheduleEntry entry : result.getScheduledEntries()) {
            System.out.println(entry.getCourse().getId() + " | " + entry.getTimeSlot().getDay()
                    + " " + entry.getTimeSlot().getStartTime() + " | Room: "
                    + entry.getRoom().getId() + " | Waste: " + entry.getWastedSeats());
        }
        System.out.println("Unscheduled:");
        for (Course course : result.getUnscheduledCourses()) System.out.println(course.getId());
        System.out.println("Total wasted seats: " + result.getTotalWastedSeats());
    }

    public void runAllDemos(ConstraintData data) {
        printResult("GREEDY SCHEDULE", new GreedySolver().solve(data));
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(data);
        System.out.println("\n===== CONFLICT GRAPH =====");
        graph.printGraph();
        List<TimeSlot> slots = new TimeSlotGenerator().generate(data.getScheduleSettings());
        Map<String, TimeSlot> assignments = new WelshPowellSolver().assignTimeSlots(graph, slots);
        System.out.println("\n===== WELSH-POWELL =====");
        for (Course course : data.getClasses()) {
            TimeSlot slot = assignments.get(course.getId());
            System.out.println(course.getId() + " -> " + (slot == null ? "UNSCHEDULED" : slot.getDay() + " " + slot.getStartTime()));
        }
        printResult("DYNAMIC PROGRAMMING", new DynamicProgrammingOptimizer().optimizeSchedule(data, assignments));
        printResult("BACKTRACKING", new BacktrackingSolver().solve(data));
    }
}
