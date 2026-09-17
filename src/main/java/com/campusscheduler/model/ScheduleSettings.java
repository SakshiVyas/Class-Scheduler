package com.campusscheduler.model;

import java.util.List;

public class ScheduleSettings {
    private List<String> workingDays;
    private String dayStartTime;
    private String dayEndTime;
    private int slotMinutes = 60;

    public List<String> getWorkingDays() { return workingDays; }
    public void setWorkingDays(List<String> workingDays) { this.workingDays = workingDays; }
    public String getDayStartTime() { return dayStartTime; }
    public void setDayStartTime(String dayStartTime) { this.dayStartTime = dayStartTime; }
    public String getDayEndTime() { return dayEndTime; }
    public void setDayEndTime(String dayEndTime) { this.dayEndTime = dayEndTime; }
    public int getSlotMinutes() { return slotMinutes; }
    public void setSlotMinutes(int slotMinutes) { this.slotMinutes = slotMinutes; }
}
