package com.campusscheduler.service;

import com.campusscheduler.api.dto.AlgorithmRun;
import com.campusscheduler.api.dto.ScheduleComparison;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.TimeSlot;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ConsoleScheduleReporter {
    public void print(ScheduleComparison comparison) {
        System.out.println("\n===== CLASS SCHEDULER COMPARISON =====");
        for (AlgorithmRun run : comparison.algorithms()) {
            printRun(run);
        }
    }

    private void printRun(AlgorithmRun run) {
        System.out.println("\n===== " + run.algorithm().toUpperCase() + " =====");
        for (ScheduleEntry entry : run.scheduledEntries()) {
            String fit = entry.getWastedSeats() == 0 ? "Perfect Fit" : "Wasted " + entry.getWastedSeats() + " seats";
            System.out.println("Scheduled " + entry.getCourse().getId() + " " + entry.getTimeSlot().getStartTime()
                    + " " + entry.getRoom().getId() + " " + fit);
        }
        if (run.scheduledEntries().isEmpty()) {
            for (Map.Entry<String, TimeSlot> entry : run.timeSlotAssignments().entrySet()) {
                System.out.println("Scheduled " + entry.getKey() + " " + entry.getValue().getStartTime() + " N/A");
            }
        }
        for (var conflict : run.conflictReport()) {
            System.out.println("Unscheduled " + conflict.courseId() + " N/A N/A " + conflict.reason());
        }
        System.out.println("Summary: " + run.scheduledCount() + " scheduled, " + run.unscheduledCount()
                + " unscheduled, wasted seats: " + (run.totalWastedSeats() == null ? "N/A" : run.totalWastedSeats())
                + ", " + run.status());
    }
}
