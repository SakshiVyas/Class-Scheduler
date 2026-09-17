package com.campusscheduler.algorithm;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.TimeSlot;
import com.campusscheduler.service.TimeSlotGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DynamicProgrammingOptimizer {
    private static final int INF = 1_000_000_000;

    public List<Course> sortCoursesBySize(List<Course> courses) {
        List<Course> sorted = new ArrayList<>(courses);
        sorted.sort(Comparator.comparingInt(Course::getEnrolledStudents));
        return sorted;
    }

    public List<Room> sortRoomsByCapacity(List<Room> rooms) {
        List<Room> sorted = new ArrayList<>(rooms);
        sorted.sort(Comparator.comparingInt(Room::getCapacity));
        return sorted;
    }

    public int[][] buildDPTable(List<Course> courses, List<Room> rooms) {
        int[][] dp = new int[courses.size() + 1][rooms.size() + 1];
        for (int j = 0; j <= rooms.size(); j++) dp[0][j] = 0;
        for (int i = 1; i <= courses.size(); i++) dp[i][0] = INF;
        for (int i = 1; i <= courses.size(); i++) {
            for (int j = 1; j <= rooms.size(); j++) {
                Course course = courses.get(i - 1);
                Room room = rooms.get(j - 1);
                dp[i][j] = dp[i][j - 1];
                if (room.getCapacity() >= course.getEnrolledStudents() && dp[i - 1][j - 1] != INF) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 1][j - 1] + room.getCapacity() - course.getEnrolledStudents());
                }
            }
        }
        return dp;
    }

    public List<ScheduleEntry> optimizeRoomsForTimeSlot(List<Course> courses, List<Room> rooms, TimeSlot slot) {
        List<Course> sortedCourses = sortCoursesBySize(courses);
        List<Room> sortedRooms = sortRoomsByCapacity(rooms);
        int[][] dp = buildDPTable(sortedCourses, sortedRooms);
        if (dp[sortedCourses.size()][sortedRooms.size()] == INF) return new ArrayList<>();
        List<ScheduleEntry> assignments = new ArrayList<>();
        int i = sortedCourses.size(), j = sortedRooms.size();
        while (i > 0 && j > 0) {
            if (dp[i][j] == dp[i][j - 1]) { j--; continue; }
            Course course = sortedCourses.get(i - 1);
            Room room = sortedRooms.get(j - 1);
            assignments.add(new ScheduleEntry(course, room, slot, room.getCapacity() - course.getEnrolledStudents()));
            i--; j--;
        }
        Collections.reverse(assignments);
        return assignments;
    }

    public ScheduleResult optimizeSchedule(ConstraintData data, Map<String, TimeSlot> assignments) {
        ScheduleResult result = new ScheduleResult();
        for (TimeSlot slot : new TimeSlotGenerator().generate(data.getScheduleSettings())) {
            List<Course> coursesForSlot = new ArrayList<>();
            for (Course course : data.getClasses()) {
                TimeSlot assigned = assignments.get(course.getId());
                if (assigned != null && assigned.getId().equals(slot.getId())) coursesForSlot.add(course);
            }
            if (coursesForSlot.isEmpty()) continue;
            List<ScheduleEntry> entries = optimizeRoomsForTimeSlot(coursesForSlot, data.getRooms(), slot);
            if (entries.isEmpty()) for (Course course : coursesForSlot) result.addUnscheduledCourse(course);
            else for (ScheduleEntry entry : entries) result.addScheduledEntry(entry);
        }
        for (Course course : data.getClasses()) if (!assignments.containsKey(course.getId())) result.addUnscheduledCourse(course);
        return result;
    }
}
