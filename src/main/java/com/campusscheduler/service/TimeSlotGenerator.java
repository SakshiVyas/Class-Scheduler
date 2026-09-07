package com.campusscheduler.service;

import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.model.TimeSlot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotGenerator {

    public List<TimeSlot> generate(
            ScheduleSettings settings
    ) {

        List<TimeSlot> timeSlots =
                new ArrayList<>();

        LocalTime dayStart =
                LocalTime.parse(
                        settings.getDayStartTime()
                );

        LocalTime dayEnd =
                LocalTime.parse(
                        settings.getDayEndTime()
                );

        int slotMinutes =
                settings.getSlotMinutes();


        for (String day :
                settings.getWorkingDays()) {

            LocalTime currentTime =
                    dayStart;

            while (currentTime.isBefore(dayEnd)) {

                String id =
                        day.toUpperCase()
                                .substring(0, 3)
                                + "_"
                                + currentTime
                                .toString()
                                .replace(":", "_");

                TimeSlot timeSlot =
                        new TimeSlot(
                                id,
                                day,
                                currentTime.toString()
                        );

                timeSlots.add(timeSlot);

                currentTime =
                        currentTime.plusMinutes(
                                slotMinutes
                        );
            }
        }

        return timeSlots;
    }
}