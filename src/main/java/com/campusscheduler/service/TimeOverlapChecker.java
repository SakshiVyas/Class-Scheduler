package com.campusscheduler.service;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.TimeSlot;

import java.time.LocalTime;

public class TimeOverlapChecker {

    public boolean overlaps(
            ClassOccurrence firstOccurrence,
            TimeSlot firstTimeSlot,
            ClassOccurrence secondOccurrence,
            TimeSlot secondTimeSlot
    ) {

        if (!firstTimeSlot.getDay()
                .equals(secondTimeSlot.getDay())) {

            return false;
        }


        LocalTime firstStart =
                LocalTime.parse(
                        firstTimeSlot.getStartTime()
                );

        LocalTime firstEnd =
                firstOccurrence.calculateEndTime(
                        firstTimeSlot
                );


        LocalTime secondStart =
                LocalTime.parse(
                        secondTimeSlot.getStartTime()
                );

        LocalTime secondEnd =
                secondOccurrence.calculateEndTime(
                        secondTimeSlot
                );


        return firstStart.isBefore(secondEnd)
                && secondStart.isBefore(firstEnd);
    }
}