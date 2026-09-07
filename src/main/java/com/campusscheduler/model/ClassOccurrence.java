package com.campusscheduler.model;
import java.time.LocalTime;
import com.campusscheduler.model.TimeSlot;

public class ClassOccurrence{

    private String id;
    private Course course;
    private int occurrenceNumber;

    public ClassOccurrence() {
    }

    public ClassOccurrence(
            String id,
            Course course,
            int occurrenceNumber
    ) {
        this.id = id;
        this.course = course;
        this.occurrenceNumber = occurrenceNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    public int getOccurrenceNumber() {
        return occurrenceNumber;
    }

    public void setOccurrenceNumber(int occurrenceNumber) {
        this.occurrenceNumber = occurrenceNumber;
    }

    public LocalTime calculateEndTime(TimeSlot timeSlot) {

        LocalTime startTime =
                LocalTime.parse(
                        timeSlot.getStartTime()
                );

        return startTime.plusMinutes(
                course.getDurationMinutes()
        );
    }

}