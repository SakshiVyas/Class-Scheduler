package com.campusscheduler.model;

import java.util.ArrayList;
import java.util.List;

public class ScheduleResult {

    private List<ScheduleEntry> scheduledEntries;
    private List<Course> unscheduledCourses;
    private int totalWastedSeats;


    public ScheduleResult() {
        this.scheduledEntries = new ArrayList<>();
        this.unscheduledCourses = new ArrayList<>();
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


    public List<Course> getUnscheduledCourses() {
        return unscheduledCourses;
    }

    public void setUnscheduledCourses(List<Course> unscheduledCourses) {
        this.unscheduledCourses = unscheduledCourses;
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


    public void addUnscheduledCourse(Course course) {
        unscheduledCourses.add(course);
    }
}
