package com.campusscheduler.algorithm;

import com.campusscheduler.model.ClassOccurrence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConflictGraph {

    private final Map<String, Set<String>> graph = new HashMap<>();


    public void addOccurrence(
            ClassOccurrence occurrence
    ) {
        graph.putIfAbsent(
                occurrence.getId(),
                new HashSet<>()
        );
    }


    public void addConflict(
            String firstOccurrenceId,
            String secondOccurrenceId
    ) {
        graph.get(firstOccurrenceId).add(secondOccurrenceId);

        graph.get(secondOccurrenceId).add(firstOccurrenceId);
    }


    private boolean haveSameProfessor(
            ClassOccurrence first,
            ClassOccurrence second
    ) {
        String firstProfessor =
                first.getCourse().getProfessorId();

        String secondProfessor =
                second.getCourse().getProfessorId();

        return firstProfessor.equals(secondProfessor);
    }


    private boolean shareStudentGroup(
            ClassOccurrence first,
            ClassOccurrence second
    ) {
        List<String> firstGroups =
                first.getCourse().getStudentGroups();

        List<String> secondGroups =
                second.getCourse().getStudentGroups();

        if (firstGroups == null || secondGroups == null) {
            return false;
        }

        for (String group : firstGroups) {

            if (secondGroups.contains(group)) {
                return true;
            }
        }

        return false;
    }


    public void buildGraph(
            List<ClassOccurrence> occurrences
    ) {

        graph.clear();

        for (ClassOccurrence occurrence : occurrences) {
            addOccurrence(occurrence);
        }

        for (int i = 0;
             i < occurrences.size();
             i++) {

            for (int j = i + 1;
                 j < occurrences.size();
                 j++) {

                ClassOccurrence first =
                        occurrences.get(i);

                ClassOccurrence second =
                        occurrences.get(j);

                if (haveSameProfessor(first, second)
                        ||
                        shareStudentGroup(first, second)) {

                    addConflict(
                            first.getId(),
                            second.getId()
                    );
                }
            }
        }
    }


    public Set<String> getOccurrences() {
        return graph.keySet();
    }


    public Set<String> getConflicts(
            String occurrenceId
    ) {
        return graph.getOrDefault(
                occurrenceId,
                new HashSet<>()
        );
    }


    public void printGraph() {

        for (String occurrenceId : graph.keySet()) {

            System.out.println(
                    occurrenceId
                            + " -> "
                            + graph.get(occurrenceId)
            );
        }
    }
}