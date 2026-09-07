package com.campusscheduler.algorithm;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.model.TimeSlot;

import com.campusscheduler.service.ClassOccurrenceGenerator;
import com.campusscheduler.service.ScheduleTimeValidator;
import com.campusscheduler.service.TimeOverlapChecker;
import com.campusscheduler.service.TimeSlotGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BacktrackingSolver {

    private List<ScheduleEntry> bestSchedule =
            new ArrayList<>();

    private int bestWaste =
            Integer.MAX_VALUE;

    private int bestSpacingPenalty =
            Integer.MAX_VALUE;

    private int totalOccurrences;

    private int theoreticalMinimumWaste;

    private boolean optimalFound;


    private final TimeOverlapChecker timeOverlapChecker =
            new TimeOverlapChecker();

    private final ScheduleTimeValidator scheduleTimeValidator =
            new ScheduleTimeValidator();


    private boolean hasEnoughCapacity(
            ClassOccurrence classOccurrence,
            Room room
    ) {

        return room.getCapacity()
                >= classOccurrence
                .getCourse()
                .getEnrolledStudents();
    }


    private boolean isRoomBusy(
            Room room,
            TimeSlot timeSlot,
            ClassOccurrence classOccurrence,
            List<ScheduleEntry> currentSchedule
    ) {

        for (ScheduleEntry entry : currentSchedule) {

            boolean sameRoom =
                    entry.getRoom()
                            .getId()
                            .equals(room.getId());

            boolean overlaps =
                    timeOverlapChecker.overlaps(
                            classOccurrence,
                            timeSlot,
                            entry.getClassOccurrence(),
                            entry.getTimeSlot()
                    );

            if (sameRoom && overlaps) {
                return true;
            }
        }

        return false;
    }


    private boolean isProfessorBusy(
            ClassOccurrence classOccurrence,
            TimeSlot timeSlot,
            List<ScheduleEntry> currentSchedule
    ) {

        String professorId =
                classOccurrence
                        .getCourse()
                        .getProfessorId();

        for (ScheduleEntry entry : currentSchedule) {

            String scheduledProfessorId =
                    entry.getClassOccurrence()
                            .getCourse()
                            .getProfessorId();

            boolean sameProfessor =
                    Objects.equals(
                            professorId,
                            scheduledProfessorId
                    );

            boolean overlaps =
                    timeOverlapChecker.overlaps(
                            classOccurrence,
                            timeSlot,
                            entry.getClassOccurrence(),
                            entry.getTimeSlot()
                    );

            if (sameProfessor && overlaps) {
                return true;
            }
        }

        return false;
    }


    private boolean shareStudentGroup(
            ClassOccurrence first,
            ClassOccurrence second
    ) {

        List<String> firstGroups =
                first.getCourse()
                        .getStudentGroups();

        List<String> secondGroups =
                second.getCourse()
                        .getStudentGroups();


        if (firstGroups == null
                || secondGroups == null) {

            return false;
        }


        for (String group : firstGroups) {

            if (secondGroups.contains(group)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasStudentGroupConflict(
            ClassOccurrence classOccurrence,
            TimeSlot timeSlot,
            List<ScheduleEntry> currentSchedule
    ) {

        for (ScheduleEntry entry : currentSchedule) {

            boolean sharesGroup =
                    shareStudentGroup(
                            classOccurrence,
                            entry.getClassOccurrence()
                    );

            boolean overlaps =
                    timeOverlapChecker.overlaps(
                            classOccurrence,
                            timeSlot,
                            entry.getClassOccurrence(),
                            entry.getTimeSlot()
                    );

            if (sharesGroup && overlaps) {
                return true;
            }
        }

        return false;
    }


    private boolean isValidAssignment(
            ClassOccurrence classOccurrence,
            Room room,
            TimeSlot timeSlot,
            List<ScheduleEntry> currentSchedule,
            ScheduleSettings settings
    ) {

        if (!scheduleTimeValidator
                .fitsWithinWorkingHours(
                        classOccurrence,
                        timeSlot,
                        settings
                )) {

            return false;
        }


        if (!hasEnoughCapacity(
                classOccurrence,
                room
        )) {

            return false;
        }


        if (isRoomBusy(
                room,
                timeSlot,
                classOccurrence,
                currentSchedule
        )) {

            return false;
        }


        if (isProfessorBusy(
                classOccurrence,
                timeSlot,
                currentSchedule
        )) {

            return false;
        }


        if (hasStudentGroupConflict(
                classOccurrence,
                timeSlot,
                currentSchedule
        )) {

            return false;
        }


        return true;
    }


    private int calculateTotalWaste(
            List<ScheduleEntry> schedule
    ) {

        int totalWaste = 0;

        for (ScheduleEntry entry : schedule) {
            totalWaste += entry.getWastedSeats();
        }

        return totalWaste;
    }


    /*
     * Counts pairs of occurrences from the same
     * course that are scheduled on the same day.
     *
     * Lower is better.
     * Zero means repeated classes are spread
     * across different days.
     */
    private int calculateSpacingPenalty(
            List<ScheduleEntry> schedule
    ) {

        int penalty = 0;


        for (int i = 0;
             i < schedule.size();
             i++) {

            ScheduleEntry first =
                    schedule.get(i);


            for (int j = i + 1;
                 j < schedule.size();
                 j++) {

                ScheduleEntry second =
                        schedule.get(j);


                String firstCourseId =
                        first.getClassOccurrence()
                                .getCourse()
                                .getId();

                String secondCourseId =
                        second.getClassOccurrence()
                                .getCourse()
                                .getId();


                boolean sameCourse =
                        firstCourseId.equals(
                                secondCourseId
                        );

                boolean sameDay =
                        first.getTimeSlot()
                                .getDay()
                                .equals(
                                        second.getTimeSlot()
                                                .getDay()
                                );


                if (sameCourse && sameDay) {
                    penalty++;
                }
            }
        }

        return penalty;
    }


    private void updateBestSchedule(
            List<ScheduleEntry> currentSchedule
    ) {

        int currentWaste =
                calculateTotalWaste(currentSchedule);

        int currentSpacingPenalty =
                calculateSpacingPenalty(currentSchedule);


        boolean betterSchedule = false;


        // Priority 1:
        // schedule as many occurrences as possible
        if (currentSchedule.size() > bestSchedule.size()) {

            betterSchedule = true;
        }


        else if (currentSchedule.size() == bestSchedule.size()) {

            // Priority 2:
            // minimize wasted room capacity
            if (currentWaste < bestWaste) {

                betterSchedule = true;
            }


            // Priority 3:
            // if waste is equal, prefer better spacing
            else if (currentWaste == bestWaste
                    && currentSpacingPenalty < bestSpacingPenalty) {

                betterSchedule = true;
            }
        }


        if (betterSchedule) {

            bestSchedule =
                    new ArrayList<>(currentSchedule);

            bestWaste =
                    currentWaste;

            bestSpacingPenalty =
                    currentSpacingPenalty;
        }


        if (bestSchedule.size() == totalOccurrences
                && bestWaste == theoreticalMinimumWaste
                && bestSpacingPenalty == 0) {

            optimalFound = true;
        }
    }


    private List<ClassOccurrence> sortOccurrencesByDifficulty(
            List<ClassOccurrence> occurrences
    ) {

        ConflictGraph graph =
                new ConflictGraph();

        graph.buildGraph(occurrences);


        List<ClassOccurrence> sorted =
                new ArrayList<>(occurrences);


        sorted.sort(
                (first, second) -> {

                    int firstDegree =
                            graph.getConflicts(
                                    first.getId()
                            ).size();

                    int secondDegree =
                            graph.getConflicts(
                                    second.getId()
                            ).size();


                    // More conflicts first
                    int comparison =
                            Integer.compare(
                                    secondDegree,
                                    firstDegree
                            );

                    if (comparison != 0) {
                        return comparison;
                    }


                    // Larger classes next
                    comparison =
                            Integer.compare(
                                    second.getCourse()
                                            .getEnrolledStudents(),
                                    first.getCourse()
                                            .getEnrolledStudents()
                            );

                    if (comparison != 0) {
                        return comparison;
                    }


                    // Longer classes next
                    return Integer.compare(
                            second.getCourse()
                                    .getDurationMinutes(),
                            first.getCourse()
                                    .getDurationMinutes()
                    );
                }
        );


        return sorted;
    }


    private List<Room> sortRoomsByCapacity(
            List<Room> rooms
    ) {

        List<Room> sortedRooms =
                new ArrayList<>(rooms);

        sortedRooms.sort(
                Comparator.comparingInt(
                        Room::getCapacity
                )
        );

        return sortedRooms;
    }


    /*
      If another occurrence of this course
     already uses Monday, try other days
      before trying Monday again.
     */
    private List<TimeSlot> orderTimeSlots(
            ClassOccurrence classOccurrence,
            List<TimeSlot> timeSlots,
            List<ScheduleEntry> currentSchedule
    ) {

        Set<String> usedDays =
                new HashSet<>();


        for (ScheduleEntry entry :
                currentSchedule) {

            String scheduledCourseId =
                    entry.getClassOccurrence()
                            .getCourse()
                            .getId();

            if (scheduledCourseId.equals(
                    classOccurrence
                            .getCourse()
                            .getId()
            )) {

                usedDays.add(
                        entry.getTimeSlot()
                                .getDay()
                );
            }
        }


        List<TimeSlot> preferred =
                new ArrayList<>();

        List<TimeSlot> alreadyUsedDays =
                new ArrayList<>();


        for (TimeSlot timeSlot : timeSlots) {

            if (usedDays.contains(
                    timeSlot.getDay()
            )) {

                alreadyUsedDays.add(
                        timeSlot
                );

            } else {

                preferred.add(
                        timeSlot
                );
            }
        }


        preferred.addAll(
                alreadyUsedDays
        );

        return preferred;
    }


    private int calculateTheoreticalMinimumWaste(
            List<ClassOccurrence> occurrences,
            List<Room> rooms
    ) {

        int totalWaste = 0;


        for (ClassOccurrence occurrence :
                occurrences) {

            int smallestWaste =
                    Integer.MAX_VALUE;


            for (Room room : rooms) {

                if (hasEnoughCapacity(
                        occurrence,
                        room
                )) {

                    int waste =
                            room.getCapacity()
                                    - occurrence
                                    .getCourse()
                                    .getEnrolledStudents();

                    smallestWaste =
                            Math.min(
                                    smallestWaste,
                                    waste
                            );
                }
            }


            if (smallestWaste
                    == Integer.MAX_VALUE) {

                return Integer.MAX_VALUE;
            }


            totalWaste += smallestWaste;
        }


        return totalWaste;
    }


    private void backtrack(
            int occurrenceIndex,
            List<ClassOccurrence> occurrences,
            List<Room> rooms,
            List<TimeSlot> timeSlots,
            List<ScheduleEntry> currentSchedule,
            ScheduleSettings settings
    ) {

        if (optimalFound) {
            return;
        }


        int remainingOccurrences =
                occurrences.size()
                        - occurrenceIndex;

        int maximumPossibleScheduled =
                currentSchedule.size()
                        + remainingOccurrences;


        // Cannot even match the best schedule.
        if (maximumPossibleScheduled
                < bestSchedule.size()) {

            return;
        }


        /*
          If this branch can only tie the
         number of scheduled occurrences,
          its current penalties must still
          have a chance to beat the best.
         */
        if (maximumPossibleScheduled
                == bestSchedule.size()) {

            int currentWaste =
                    calculateTotalWaste(
                            currentSchedule
                    );

            int currentSpacingPenalty =
                    calculateSpacingPenalty(
                            currentSchedule
                    );

            if (currentWaste > bestWaste) {
                return;
            }

            if (currentWaste == bestWaste
                    && currentSpacingPenalty >= bestSpacingPenalty) {

                return;
            }

        }


        if (occurrenceIndex
                == occurrences.size()) {

            updateBestSchedule(
                    currentSchedule
            );

            return;
        }


        ClassOccurrence classOccurrence =
                occurrences.get(
                        occurrenceIndex
                );


        List<TimeSlot> orderedTimeSlots =
                orderTimeSlots(
                        classOccurrence,
                        timeSlots,
                        currentSchedule
                );


        for (TimeSlot timeSlot :
                orderedTimeSlots) {

            for (Room room : rooms) {

                if (isValidAssignment(
                        classOccurrence,
                        room,
                        timeSlot,
                        currentSchedule,
                        settings
                )) {

                    int wastedSeats =
                            room.getCapacity()
                                    - classOccurrence
                                    .getCourse()
                                    .getEnrolledStudents();


                    ScheduleEntry entry =
                            new ScheduleEntry(
                                    classOccurrence,
                                    room,
                                    timeSlot,
                                    wastedSeats
                            );


                    currentSchedule.add(entry);


                    backtrack(
                            occurrenceIndex + 1,
                            occurrences,
                            rooms,
                            timeSlots,
                            currentSchedule,
                            settings
                    );


                    currentSchedule.remove(
                            currentSchedule.size()
                                    - 1
                    );


                    if (optimalFound) {
                        return;
                    }
                }
            }
        }


        // Best-effort branch:
        // leave this occurrence unscheduled.
        backtrack(
                occurrenceIndex + 1,
                occurrences,
                rooms,
                timeSlots,
                currentSchedule,
                settings
        );
    }


    private boolean isOccurrenceScheduled(
            ClassOccurrence occurrence,
            List<ScheduleEntry> schedule
    ) {

        for (ScheduleEntry entry : schedule) {

            if (entry.getClassOccurrence()
                    .getId()
                    .equals(
                            occurrence.getId()
                    )) {

                return true;
            }
        }

        return false;
    }


    public ScheduleResult solve(
            ConstraintData data
    ) {

        optimalFound = false;


        ClassOccurrenceGenerator occurrenceGenerator =
                new ClassOccurrenceGenerator();

        List<ClassOccurrence> generatedOccurrences =
                occurrenceGenerator.generate(
                        data.getClasses()
                );


        List<ClassOccurrence> occurrences =
                sortOccurrencesByDifficulty(
                        generatedOccurrences
                );


        List<Room> sortedRooms =
                sortRoomsByCapacity(
                        data.getRooms()
                );


        TimeSlotGenerator timeSlotGenerator =
                new TimeSlotGenerator();

        List<TimeSlot> timeSlots =
                timeSlotGenerator.generate(
                        data.getScheduleSettings()
                );


        totalOccurrences =
                occurrences.size();

        theoreticalMinimumWaste =
                calculateTheoreticalMinimumWaste(
                        occurrences,
                        sortedRooms
                );


        /*
         * Seed branch-and-bound using Greedy.
         * This gives Backtracking a useful
         * solution before exhaustive search.
         */
        GreedySolver greedySolver =
                new GreedySolver();

        ScheduleResult greedyResult =
                greedySolver.solve(data);


        bestSchedule =
                new ArrayList<>(
                        greedyResult
                                .getScheduledEntries()
                );

        bestWaste =
                calculateTotalWaste(
                        bestSchedule
                );

        bestSpacingPenalty =
                calculateSpacingPenalty(
                        bestSchedule
                );


        if (bestSchedule.size()
                == totalOccurrences
                && bestSpacingPenalty == 0
                && bestWaste
                == theoreticalMinimumWaste) {

            optimalFound = true;
        }


        if (!optimalFound) {

            List<ScheduleEntry> currentSchedule =
                    new ArrayList<>();


            backtrack(
                    0,
                    occurrences,
                    sortedRooms,
                    timeSlots,
                    currentSchedule,
                    data.getScheduleSettings()
            );
        }


        ScheduleResult result =
                new ScheduleResult();


        for (ScheduleEntry entry :
                bestSchedule) {

            result.addScheduledEntry(
                    entry
            );
        }


        for (ClassOccurrence occurrence :
                occurrences) {

            if (!isOccurrenceScheduled(
                    occurrence,
                    bestSchedule
            )) {

                result.addUnscheduledOccurrence(
                        occurrence
                );
            }
        }


        return result;
    }
}