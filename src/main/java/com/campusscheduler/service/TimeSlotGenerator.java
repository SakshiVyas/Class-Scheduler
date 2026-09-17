package com.campusscheduler.service;

import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.model.TimeSlot;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Generates non-overlapping fixed slots from the calendar settings in constraints.json. */
public class TimeSlotGenerator {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public List<TimeSlot> generate(ScheduleSettings settings) {
        if (settings == null || settings.getWorkingDays() == null) return List.of();
        int minutes = settings.getSlotMinutes() > 0 ? settings.getSlotMinutes() : 60;
        LocalTime start = LocalTime.parse(settings.getDayStartTime(), FORMAT);
        LocalTime end = LocalTime.parse(settings.getDayEndTime(), FORMAT);
        List<TimeSlot> result = new ArrayList<>();
        for (String day : settings.getWorkingDays()) {
            LocalTime current = start;
            while (current.plusMinutes(minutes).compareTo(end) <= 0) {
                String prefix = day.substring(0, Math.min(3, day.length())).toUpperCase();
                result.add(new TimeSlot(prefix + "_" + String.format("%02d", current.getHour()), day,
                        current.format(FORMAT)));
                current = current.plusMinutes(minutes);
            }
        }
        return result;
    }
}
