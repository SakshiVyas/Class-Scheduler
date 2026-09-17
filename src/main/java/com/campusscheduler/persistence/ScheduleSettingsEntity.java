package com.campusscheduler.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule_settings")
public class ScheduleSettingsEntity {
    @Id
    private Integer id;
    private String workingDays;
    private String dayStartTime;
    private String dayEndTime;
    private int slotMinutes;

    protected ScheduleSettingsEntity() {}

    public Integer getId() { return id; }
    public String getWorkingDays() { return workingDays; }
    public String getDayStartTime() { return dayStartTime; }
    public String getDayEndTime() { return dayEndTime; }
    public int getSlotMinutes() { return slotMinutes; }
}
