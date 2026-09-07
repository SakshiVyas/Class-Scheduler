package com.campusscheduler.model;

import java.util.ArrayList;
import java.util.List;

public class ScheduleResult {

    private List<ScheduleEntry> scheduledEntries;
    private List<ClassOccurrence> unscheduledOccurrences;
    private int totalWastedSeats;


    public ScheduleResult() {
        this.scheduledEntries = new ArrayList<>();
        this.unscheduledOccurrences = new ArrayList<>();
        this.totalWastedSeats = 0;
    }


    public List<ScheduleEntry> getScheduledEntries() {
        return scheduledEntries;
    }

    public void setScheduledEntries(
            List<ScheduleEntry> scheduledEntries
    ) {
        this.scheduledEntries = scheduledEntries;
    }


    public List<ClassOccurrence> getUnscheduledOccurrences() {
        return unscheduledOccurrences;
    }

    public void setUnscheduledOccurrences(
            List<ClassOccurrence> unscheduledOccurrences
    ) {
        this.unscheduledOccurrences = unscheduledOccurrences;
    }


    public int getTotalWastedSeats() {
        return totalWastedSeats;
    }

    public void setTotalWastedSeats(int totalWastedSeats) {
        this.totalWastedSeats = totalWastedSeats;
    }


    public void addScheduledEntry(
            ScheduleEntry entry
    ) {
        scheduledEntries.add(entry);

        totalWastedSeats +=
                entry.getWastedSeats();
    }


    public void addUnscheduledOccurrence(
            ClassOccurrence occurrence
    ) {
        unscheduledOccurrences.add(occurrence);
    }
}