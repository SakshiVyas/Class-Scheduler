package com.campusscheduler.algorithm;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConflictGraph {
    private final Map<String, Set<String>> graph = new HashMap<>();

    public void buildGraph(List<Course> courses, Map<String, List<String>> studentGroups) {
        graph.clear();
        for (Course course : courses) graph.putIfAbsent(course.getId(), new HashSet<>());
        for (int i = 0; i < courses.size(); i++) {
            for (int j = i + 1; j < courses.size(); j++) {
                Course first = courses.get(i), second = courses.get(j);
                if (first.getProfessorId().equals(second.getProfessorId())
                        || ScheduleValidator.shareStudentGroup(first, second, studentGroups)) {
                    addConflict(first.getId(), second.getId());
                }
            }
        }
    }

    public void buildGraph(ConstraintData data) {
        buildGraph(data.getClasses(), data.getStudentGroups());
    }

    public void addConflict(String first, String second) {
        graph.get(first).add(second);
        graph.get(second).add(first);
    }

    public Set<String> getCourses() {
        return graph.keySet();
    }

    public Set<String> getConflicts(String courseId) {
        return graph.getOrDefault(courseId, Set.of());
    }
}
