package com.campusscheduler.model;

public class TimeSlot {
    private String id;
    private String day;
    private String startTime;

    public TimeSlot() {
    }

    public TimeSlot(String id, String day, String startTime) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
