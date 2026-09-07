package com.campusscheduler.algorithm;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.model.TimeSlot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WelshPowellSolver {

    public List<String> sortOccurrencesByDegree(
            ConflictGraph graph
    ) {
        List<String> occurrences =
                new ArrayList<>(graph.getOccurrences());

        occurrences.sort(
                Comparator.comparingInt(
                        (String occurrenceId) ->
                                graph.getConflicts(
                                        occurrenceId
                                ).size()
                ).reversed()
        );

        return occurrences;
    }


    private Set<Integer> getUsedNeighborColors(
            String occurrenceId,
            ConflictGraph graph,
            Map<String, Integer> colors
    ) {
        Set<Integer> usedColors =
                new HashSet<>();

        for (String neighborId :
                graph.getConflicts(occurrenceId)) {

            if (colors.containsKey(neighborId)) {
                usedColors.add(
                        colors.get(neighborId)
                );
            }
        }

        return usedColors;
    }


    private int getSmallestAvailableColor(
            Set<Integer> usedColors
    ) {
        int color = 0;

        while (usedColors.contains(color)) {
            color++;
        }

        return color;
    }


    public Map<String, Integer> colorGraph(
            ConflictGraph graph
    ) {
        Map<String, Integer> colors =
                new HashMap<>();

        List<String> sortedOccurrences =
                sortOccurrencesByDegree(graph);

        for (String occurrenceId :
                sortedOccurrences) {

            Set<Integer> usedColors =
                    getUsedNeighborColors(
                            occurrenceId,
                            graph,
                            colors
                    );

            int color =
                    getSmallestAvailableColor(
                            usedColors
                    );

            colors.put(
                    occurrenceId,
                    color
            );
        }

        return colors;
    }

    public Map<String, TimeSlot> assignTimeSlots(
            ConflictGraph graph,
            List<ClassOccurrence> occurrences,
            List<TimeSlot> timeSlots,
            ScheduleSettings settings
    ) {

        Map<String, TimeSlot> assignments =
                new HashMap<>();

        Map<String, Integer> colors =
                colorGraph(graph);

        int maximumDuration =
                getMaximumDuration(occurrences);

        int slotMinutes =
                settings.getSlotMinutes();

        int blockMinutes =
                (int) Math.ceil(
                        (double) maximumDuration / slotMinutes
                ) * slotMinutes;


        List<TimeSlot> safeColorSlots =
                new ArrayList<>();

        LocalTime dayStart =
                LocalTime.parse(
                        settings.getDayStartTime()
                );

        LocalTime dayEnd =
                LocalTime.parse(
                        settings.getDayEndTime()
                );


        for (TimeSlot timeSlot : timeSlots) {

            LocalTime start =
                    LocalTime.parse(
                            timeSlot.getStartTime()
                    );

            LocalTime end =
                    start.plusMinutes(blockMinutes);


            long minutesFromDayStart =
                    java.time.Duration
                            .between(dayStart, start)
                            .toMinutes();


            boolean aligned =
                    minutesFromDayStart % blockMinutes == 0;

            boolean fitsInDay =
                    !end.isAfter(dayEnd);


            if (aligned && fitsInDay) {
                safeColorSlots.add(timeSlot);
            }
        }


        for (Map.Entry<String, Integer> entry :
                colors.entrySet()) {

            String occurrenceId =
                    entry.getKey();

            int color =
                    entry.getValue();


            if (color < safeColorSlots.size()) {

                assignments.put(
                        occurrenceId,
                        safeColorSlots.get(color)
                );
            }
        }

        return assignments;
    }

    private int getMaximumDuration(
            List<ClassOccurrence> occurrences
    ) {

        int maximumDuration = 0;

        for (ClassOccurrence occurrence : occurrences) {

            int duration =
                    occurrence.getCourse()
                            .getDurationMinutes();

            if (duration > maximumDuration) {
                maximumDuration = duration;
            }
        }

        return maximumDuration;
    }
}