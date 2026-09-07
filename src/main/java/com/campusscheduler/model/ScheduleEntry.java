package com.campusscheduler.model;

public class ScheduleEntry {

    private ClassOccurrence classOccurrence;
    private Room room;
    private TimeSlot timeSlot;
    private int wastedSeats;


    public ScheduleEntry() {
    }


    public ScheduleEntry(
            ClassOccurrence classOccurrence,
            Room room,
            TimeSlot timeSlot,
            int wastedSeats
    ) {
        this.classOccurrence = classOccurrence;
        this.room = room;
        this.timeSlot = timeSlot;
        this.wastedSeats = wastedSeats;
    }


    public ClassOccurrence getClassOccurrence() {
        return classOccurrence;
    }

    public void setClassOccurrence(ClassOccurrence classOccurrence) {
        this.classOccurrence = classOccurrence;
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