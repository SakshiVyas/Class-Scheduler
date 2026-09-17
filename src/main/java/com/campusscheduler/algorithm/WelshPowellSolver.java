package com.campusscheduler.algorithm;

import com.campusscheduler.model.TimeSlot;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class WelshPowellSolver {
    public List<String> sortCoursesByDegree(ConflictGraph graph) {
        List<String> courses = new ArrayList<>(graph.getCourses());
        courses.sort(Comparator.comparingInt((String id) -> graph.getConflicts(id).size()).reversed());
        return courses;
    }

    public Map<String, Integer> colorGraph(ConflictGraph graph) {
        Map<String, Integer> colors = new HashMap<>();
        for (String courseId : sortCoursesByDegree(graph)) {
            Set<Integer> used = new HashSet<>();
            for (String neighbor : graph.getConflicts(courseId)) if (colors.containsKey(neighbor)) used.add(colors.get(neighbor));
            int color = 0;
            while (used.contains(color)) color++;
            colors.put(courseId, color);
        }
        return colors;
    }

    public Map<String, TimeSlot> assignTimeSlots(ConflictGraph graph, List<TimeSlot> timeSlots) {
        return assignTimeSlots(graph, timeSlots, Integer.MAX_VALUE);
    }

    public Map<String, TimeSlot> assignTimeSlots(ConflictGraph graph, List<TimeSlot> timeSlots,
                                                  int maximumCoursesPerSlot) {
        Map<String, TimeSlot> assignments = new HashMap<>();
        Map<Integer, List<String>> coursesByColor = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : colorGraph(graph).entrySet()) {
            coursesByColor.computeIfAbsent(entry.getValue(), ignored -> new ArrayList<>()).add(entry.getKey());
        }
        int slotIndex = 0;
        int slotCapacity = Math.max(1, maximumCoursesPerSlot);
        for (List<String> courses : coursesByColor.values()) {
            courses.sort(String::compareTo);
            int slotsRequired = (courses.size() + slotCapacity - 1) / slotCapacity;
            for (int i = 0; i < courses.size(); i++) {
                int assignedSlot = slotIndex + i % slotsRequired;
                if (assignedSlot < timeSlots.size()) assignments.put(courses.get(i), timeSlots.get(assignedSlot));
            }
            slotIndex += slotsRequired;
        }
        return assignments;
    }
}
