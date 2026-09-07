package com.campusscheduler.algorithm;

import com.campusscheduler.model.*;
import com.campusscheduler.service.ClassOccurrenceGenerator;
import com.campusscheduler.service.TimeSlotGenerator;

import java.util.*;

public class DynamicProgrammingOptimizer {

    private static final int INF= 1000000000;

    public List<ClassOccurrence> sortOccurrencesBySize(
            List<ClassOccurrence> occurrences
    ) {
        List<ClassOccurrence> sortedOccurrences =
                new ArrayList<>(occurrences);

        sortedOccurrences.sort(
                Comparator.comparingInt(
                        occurrence ->
                                occurrence.getCourse()
                                        .getEnrolledStudents()
                )
        );

        return sortedOccurrences;
    }

    public List<Room> sortRoomsByCapacity(List<Room> rooms) {
        List<Room> sortedRooms = new ArrayList<>(rooms);
        sortedRooms.sort(
                Comparator.comparingInt(
                        Room::getCapacity
                )
        );
        return sortedRooms;
    }
    // create DP table
    private int[][] createDPTable( int numberOfCourses, int numberOfRooms){
         int [][] dp = new int[numberOfCourses + 1][numberOfRooms + 1];
         // cases with either no courses or no rooms
         for(int j=0; j<= numberOfRooms;j++){
             dp[0][j] = 0;
         }
         for (int i=1;i<=numberOfCourses;i++){
             dp[i][0] = INF;
         }
         return dp;
    }
    private int calculateWaste(
            ClassOccurrence occurrence,
            Room room
    ) {
        return room.getCapacity()
                - occurrence.getCourse()
                .getEnrolledStudents();
    }
    // build table

    public int[][] buildDPTable(
            List<ClassOccurrence> occurrences,
            List<Room> rooms
    ) {
        int[][] dp =
                createDPTable(
                        occurrences.size(),
                        rooms.size()
                );

        for (int i = 1;
             i <= occurrences.size();
             i++) {

            for (int j = 1;
                 j <= rooms.size();
                 j++) {

                ClassOccurrence occurrence =
                        occurrences.get(i - 1);

                Room room =
                        rooms.get(j - 1);

                // Option 1:
                // do not use this room
                dp[i][j] =
                        dp[i][j - 1];

                // Option 2:
                // assign this room
                if (room.getCapacity()
                        >= occurrence.getCourse()
                        .getEnrolledStudents()) {

                    if (dp[i - 1][j - 1] != INF) {

                        int waste =
                                calculateWaste(
                                        occurrence,
                                        room
                                );

                        int useRoom =
                                dp[i - 1][j - 1]
                                        + waste;

                        dp[i][j] =
                                Math.min(
                                        dp[i][j],
                                        useRoom
                                );
                    }
                }
            }
        }

        return dp;
    }

    public void printDPTable(
            List<ClassOccurrence> occurrences,
            List<Room> rooms
    ) {
        int[][] dp = buildDPTable(
                occurrences,
                rooms
        );

        for (int i = 0; i < dp.length; i++) {

            for (int j = 0; j < dp[i].length; j++) {

                if (dp[i][j] == INF) {
                    System.out.print("INF\t");
                } else {
                    System.out.print(
                            dp[i][j] + "\t"
                    );
                }
            }

            System.out.println();
        }
    }
    public List<ScheduleEntry> reconstructAssignments(
            List<ClassOccurrence> occurrences,
            List<Room> rooms,
            TimeSlot timeSlot,
            int[][] dp
    ) {
        List<ScheduleEntry> assignments =
                new ArrayList<>();

        int i = occurrences.size();
        int j = rooms.size();

        while (i > 0 && j > 0) {

            if (dp[i][j] == dp[i][j - 1]) {

                j--;

            } else {

                ClassOccurrence occurrence =
                        occurrences.get(i - 1);

                Room room =
                        rooms.get(j - 1);

                int waste =
                        calculateWaste(
                                occurrence,
                                room
                        );

                ScheduleEntry entry =
                        new ScheduleEntry(
                                occurrence,
                                room,
                                timeSlot,
                                waste
                        );

                assignments.add(entry);

                i--;
                j--;
            }
        }

        Collections.reverse(assignments);

        return assignments;
    }
    // final method

    public List<ScheduleEntry> optimizeRoomsForTimeSlot(
            List<ClassOccurrence> occurrences,
            List<Room> rooms,
            TimeSlot timeSlot
    ) {
        List<ClassOccurrence> sortedOccurrences =
                sortOccurrencesBySize(occurrences);

        List<Room> sortedRooms =
                sortRoomsByCapacity(rooms);

        int[][] dp =
                buildDPTable(
                        sortedOccurrences,
                        sortedRooms
                );

        if (!hasValidSolution(dp)) {
            return new ArrayList<>();
        }

        return reconstructAssignments(
                sortedOccurrences,
                sortedRooms,
                timeSlot,
                dp
        );
    }

    // courses belonging to a timeslot
    public List<ClassOccurrence> getOccurrencesForTimeSlot(
            List<ClassOccurrence> occurrences,
            Map<String, TimeSlot> timeAssignments,
            TimeSlot timeSlot
    ) {
        List<ClassOccurrence> result =
                new ArrayList<>();

        for (ClassOccurrence occurrence : occurrences) {

            TimeSlot assignedTime =
                    timeAssignments.get(
                            occurrence.getId()
                    );

            if (assignedTime != null
                    &&
                    assignedTime.getId()
                            .equals(timeSlot.getId())) {

                result.add(occurrence);
            }
        }

        return result;
    }

    //check if assignment is possible
    private boolean hasValidSolution(int[][] dp) {

        int lastRow = dp.length - 1;
        int lastColumn = dp[0].length - 1;

        return dp[lastRow][lastColumn] != INF;
    }

    // schedule result
    public ScheduleResult optimizeSchedule(
            ConstraintData data,
            Map<String, TimeSlot> timeAssignments
    ) {

        ScheduleResult result =
                new ScheduleResult();


        ClassOccurrenceGenerator occurrenceGenerator =
                new ClassOccurrenceGenerator();

        List<ClassOccurrence> occurrences =
                occurrenceGenerator.generate(
                        data.getClasses()
                );


        TimeSlotGenerator timeSlotGenerator =
                new TimeSlotGenerator();

        List<TimeSlot> timeSlots =
                timeSlotGenerator.generate(
                        data.getScheduleSettings()
                );


        for (TimeSlot timeSlot : timeSlots) {

            List<ClassOccurrence> occurrencesForSlot =
                    getOccurrencesForTimeSlot(
                            occurrences,
                            timeAssignments,
                            timeSlot
                    );


            if (occurrencesForSlot.isEmpty()) {
                continue;
            }


            List<ScheduleEntry> assignments =
                    optimizeRoomsForTimeSlot(
                            occurrencesForSlot,
                            data.getRooms(),
                            timeSlot
                    );


            if (assignments.isEmpty()) {

                for (ClassOccurrence occurrence :
                        occurrencesForSlot) {

                    result.addUnscheduledOccurrence(
                            occurrence
                    );
                }

                continue;
            }


            for (ScheduleEntry entry : assignments) {

                result.addScheduledEntry(entry);
            }
        }


        // Any occurrence that Welsh-Powell could not
        // assign a time to is unscheduled.
        for (ClassOccurrence occurrence : occurrences) {

            if (!timeAssignments.containsKey(
                    occurrence.getId()
            )) {

                result.addUnscheduledOccurrence(
                        occurrence
                );
            }
        }

        // Anything Welsh-Powell could not give a timeslot
        // must also be reported as unscheduled.
        for (ClassOccurrence occurrence : occurrences) {

            if (!timeAssignments.containsKey(
                    occurrence.getId()
            )) {
                result.addUnscheduledOccurrence(
                        occurrence
                );
            }
        }

        return result;
    }
}
