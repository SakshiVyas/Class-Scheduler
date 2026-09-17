package com.campusscheduler.model;

import java.util.List;
import java.util.Map;


public class ConstraintData {
    private List<Course> classes;
    private List<Room> rooms;
    private ScheduleSettings scheduleSettings;
    private Map<String, List<String>> studentGroups;

    public ConstraintData() {

    }

    public List<Course> getClasses() {
        return classes;
    }

    public void setClasses(List<Course> classes) {
        this.classes = classes;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public ScheduleSettings getScheduleSettings() {
        return scheduleSettings;
    }

    public void setScheduleSettings(ScheduleSettings scheduleSettings) {
        this.scheduleSettings = scheduleSettings;
    }

    public Map<String, List<String>> getStudentGroups() {
        return studentGroups;
    }

    public void setStudentGroups(Map<String, List<String>> studentGroups) {
        this.studentGroups = studentGroups;
    }
}
