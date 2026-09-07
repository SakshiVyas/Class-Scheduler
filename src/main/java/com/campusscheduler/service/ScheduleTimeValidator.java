package com.campusscheduler.service;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.model.TimeSlot;

import java.time.LocalTime;

public class ScheduleTimeValidator {

    public boolean fitsWithinWorkingHours(
            ClassOccurrence classOccurrence,
            TimeSlot timeSlot,
            ScheduleSettings settings
    ) {

        LocalTime dayStart =
                LocalTime.parse(
                        settings.getDayStartTime()
                );

        LocalTime dayEnd =
                LocalTime.parse(
                        settings.getDayEndTime()
                );

        LocalTime classStart =
                LocalTime.parse(
                        timeSlot.getStartTime()
                );

        LocalTime classEnd =
                classOccurrence.calculateEndTime(
                        timeSlot
                );


        boolean startsOnTime =
                !classStart.isBefore(dayStart);

        boolean endsOnTime =
                !classEnd.isAfter(dayEnd);


        return startsOnTime && endsOnTime;
    }
}