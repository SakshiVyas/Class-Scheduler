package com.campusscheduler.algorithm;

import com.campusscheduler.model.TimeSlot;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Map<String, TimeSlot> assignments = new HashMap<>();
        for (Map.Entry<String, Integer> entry : colorGraph(graph).entrySet()) {
            if (entry.getValue() < timeSlots.size()) assignments.put(entry.getKey(), timeSlots.get(entry.getValue()));
        }
        return assignments;
    }
}
