package com.campusscheduler.model;

import java.util.ArrayList;
import java.util.List;

public class ScheduleResult {
    private List<ScheduleEntry> scheduledEntries = new ArrayList<>();
    private List<Course> unscheduledCourses = new ArrayList<>();
    private int totalWastedSeats;

    public ScheduleResult() {
    }

    public List<ScheduleEntry> getScheduledEntries() {
        return scheduledEntries;
    }

    public void setScheduledEntries(List<ScheduleEntry> scheduledEntries) {
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

    public void addScheduledEntry(ScheduleEntry entry) {
        scheduledEntries.add(entry);
        totalWastedSeats += entry.getWastedSeats();
    }

    public void addUnscheduledCourse(Course course) {
        unscheduledCourses.add(course);
    }
}
