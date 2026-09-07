package com.campusscheduler.demo;

import com.campusscheduler.algorithm.*;
import com.campusscheduler.model.*;
import com.campusscheduler.service.ClassOccurrenceGenerator;
import com.campusscheduler.service.TimeSlotGenerator;

import java.util.List;
import java.util.Map;

public class AlgorithmDemo {
   // greedy

    public void runGreedyDemo(
            ConstraintData data
    ) {
        GreedySolver solver =
                new GreedySolver();

        ScheduleResult result =
                solver.solve(data);


        System.out.println();
        System.out.println("===== GREEDY SCHEDULE =====");


        for (ScheduleEntry entry :
                result.getScheduledEntries()) {

            System.out.println(
                    entry.getClassOccurrence().getId()
                            + " | "
                            + entry.getTimeSlot().getDay()
                            + " "
                            + entry.getTimeSlot().getStartTime()
                            + " | Room: "
                            + entry.getRoom().getId()
                            + " | Waste: "
                            + entry.getWastedSeats()
            );
        }


        System.out.println();
        System.out.println("Unscheduled:");

        for (ClassOccurrence occurrence :
                result.getUnscheduledOccurrences()) {

            System.out.println(
                    occurrence.getId()
            );
        }


        System.out.println(
                "Total wasted seats: "
                        + result.getTotalWastedSeats()
        );
    }

    // graphcoloring

    public Map<String, TimeSlot> runGraphColoringDemo(
            ConstraintData data
    ) {
        ClassOccurrenceGenerator generator =
                new ClassOccurrenceGenerator();

        List<ClassOccurrence> occurrences =
                generator.generate(data.getClasses());

        TimeSlotGenerator timeSlotGenerator =
                new TimeSlotGenerator();

        List<TimeSlot> timeSlots =
                timeSlotGenerator.generate(
                        data.getScheduleSettings()
                );
        ConflictGraph graph =
                new ConflictGraph();

        graph.buildGraph(occurrences);


        System.out.println();
        System.out.println("===== CONFLICT GRAPH =====");

        graph.printGraph();


        WelshPowellSolver solver =
                new WelshPowellSolver();

        Map<String, TimeSlot> assignments =
                solver.assignTimeSlots(
                        graph,
                        occurrences,
                        timeSlots,
                        data.getScheduleSettings()
                );

        System.out.println();
        System.out.println("===== WELSH-POWELL =====");

        for (ClassOccurrence occurrence : occurrences) {

            TimeSlot timeSlot =
                    assignments.get(
                            occurrence.getId()
                    );

            if (timeSlot != null) {

                System.out.println(
                        occurrence.getId()
                                + " -> "
                                + timeSlot.getDay()
                                + " "
                                + timeSlot.getStartTime()
                );

            } else {

                System.out.println(
                        occurrence.getId()
                                + " -> UNSCHEDULED"
                );
            }
        }

        return assignments;
    }


     // DP
     public void runDynamicProgrammingDemo(
             ConstraintData data,
             Map<String, TimeSlot> timeAssignments
     ) {
         DynamicProgrammingOptimizer optimizer =
                 new DynamicProgrammingOptimizer();

         ScheduleResult result =
                 optimizer.optimizeSchedule(
                         data,
                         timeAssignments
                 );


         System.out.println();
         System.out.println(
                 "===== DYNAMIC PROGRAMMING ====="
         );


         for (ScheduleEntry entry :
                 result.getScheduledEntries()) {

             System.out.println(
                     entry.getClassOccurrence().getId()
                             + " | "
                             + entry.getTimeSlot().getDay()
                             + " "
                             + entry.getTimeSlot().getStartTime()
                             + " | Room: "
                             + entry.getRoom().getId()
                             + " | Waste: "
                             + entry.getWastedSeats()
             );
         }


         System.out.println();
         System.out.println("Unscheduled:");

         for (ClassOccurrence occurrence :
                 result.getUnscheduledOccurrences()) {

             System.out.println(
                     occurrence.getId()
             );
         }


         System.out.println(
                 "Total wasted seats: "
                         + result.getTotalWastedSeats()
         );
     }
 // backtracking
    public void runBacktrackingDemo(
            ConstraintData data
    ) {
        BacktrackingSolver solver =
                new BacktrackingSolver();

        ScheduleResult result =
                solver.solve(data);


        System.out.println();
        System.out.println(
                "===== BACKTRACKING ====="
        );


        for (ScheduleEntry entry :
                result.getScheduledEntries()) {

            System.out.println(
                    entry.getClassOccurrence().getId()
                            + " | "
                            + entry.getTimeSlot().getDay()
                            + " "
                            + entry.getTimeSlot().getStartTime()
                            + " | Room: "
                            + entry.getRoom().getId()
                            + " | Waste: "
                            + entry.getWastedSeats()
            );
        }


        System.out.println();
        System.out.println("Unscheduled:");

        for (ClassOccurrence occurrence :
                result.getUnscheduledOccurrences()) {

            System.out.println(
                    occurrence.getId()
            );
        }


        System.out.println(
                "Total wasted seats: "
                        + result.getTotalWastedSeats()
        );
    }

     //all
     public void runAllDemos(
             ConstraintData data
     ) {

         runGreedyDemo(data);

         Map<String, TimeSlot> timeAssignments =
                 runGraphColoringDemo(data);

         runDynamicProgrammingDemo(
                 data,
                 timeAssignments
         );

         runBacktrackingDemo(data);
     }

}