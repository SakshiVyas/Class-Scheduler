package com.campusscheduler.api.dto;

import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.TimeSlot;
import java.util.List;
import java.util.Map;

public record AlgorithmRun(
        String algorithm,
        long runtimeMillis,
        int scheduledCount,
        int unscheduledCount,
        Integer totalWastedSeats,
        String status,
        List<ScheduleEntry> scheduledEntries,
        Map<String, TimeSlot> timeSlotAssignments,
        List<ConflictReportEntry> conflictReport
) {}
