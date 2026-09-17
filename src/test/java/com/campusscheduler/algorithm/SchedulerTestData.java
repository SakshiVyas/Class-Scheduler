package com.campusscheduler.algorithm;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.TimeSlot;
import com.campusscheduler.model.ScheduleSettings;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SchedulerTestData {
    private SchedulerTestData() {}

    static Course course(String id, int students, String professor) {
        return new Course(id, students, professor);
    }

    static TimeSlot slot(String id, String day, String start) {
        return new TimeSlot(id, day, start);
    }

    static ConstraintData data(List<Course> courses, List<Room> rooms, List<TimeSlot> slots,
                               Map<String, List<String>> groups) {
        ConstraintData data = new ConstraintData();
        data.setClasses(courses);
        data.setRooms(rooms);
        ScheduleSettings settings = new ScheduleSettings();
        String day = slots.isEmpty() ? "Monday" : slots.get(0).getDay();
        String start = slots.isEmpty() ? "09:00" : slots.get(0).getStartTime();
        int firstHour = Integer.parseInt(start.substring(0, 2));
        int lastHour = slots.isEmpty() ? firstHour + 1 : Integer.parseInt(slots.get(slots.size() - 1).getStartTime().substring(0, 2)) + 1;
        settings.setWorkingDays(List.of(day));
        settings.setDayStartTime(String.format("%02d:00", firstHour));
        settings.setDayEndTime(String.format("%02d:00", lastHour));
        settings.setSlotMinutes(60);
        data.setScheduleSettings(settings);
        data.setStudentGroups(groups);
        return data;
    }

    static Map<String, List<String>> groups(Object... values) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            result.put((String) values[i], (List<String>) values[i + 1]);
        }
        return result;
    }
}
