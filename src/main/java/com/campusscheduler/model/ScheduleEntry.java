package com.campusscheduler.model;

public class ScheduleEntry {
    private Course course;
    private Room room;
    private TimeSlot timeSlot;
    private int wastedSeats;

    public ScheduleEntry() {
    }

    public ScheduleEntry(Course course, Room room, TimeSlot timeSlot, int wastedSeats) {
        this.course = course;
        this.room = room;
        this.timeSlot = timeSlot;
        this.wastedSeats = wastedSeats;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getWastedSeats() {
        return wastedSeats;
    }

    public void setWastedSeats(int wastedSeats) {
        this.wastedSeats = wastedSeats;
    }
}
