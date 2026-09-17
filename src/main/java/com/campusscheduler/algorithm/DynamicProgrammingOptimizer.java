package com.campusscheduler.algorithm;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.TimeSlot;
import com.campusscheduler.service.TimeSlotGenerator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicProgrammingOptimizer {
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

    public List<ScheduleEntry> optimizeRoomsForTimeSlot(List<Course> courses, List<Room> rooms, TimeSlot slot) {
        List<Course> sortedCourses = sortCoursesBySize(courses);
        List<Room> sortedRooms = sortRoomsByCapacity(rooms);
        int courseCount = sortedCourses.size();
        int roomCount = sortedRooms.size();
        int[][] assigned = new int[courseCount + 1][roomCount + 1];
        int[][] waste = new int[courseCount + 1][roomCount + 1];
        byte[][] action = new byte[courseCount + 1][roomCount + 1];

        for (int i = 1; i <= courseCount; i++) {
            for (int j = 1; j <= roomCount; j++) {
                assigned[i][j] = assigned[i - 1][j];
                waste[i][j] = waste[i - 1][j];
                action[i][j] = 1;

                if (better(assigned[i][j - 1], waste[i][j - 1], assigned[i][j], waste[i][j])) {
                    assigned[i][j] = assigned[i][j - 1];
                    waste[i][j] = waste[i][j - 1];
                    action[i][j] = 2;
                }

                Course course = sortedCourses.get(i - 1);
                Room room = sortedRooms.get(j - 1);
                if (room.getCapacity() >= course.getEnrolledStudents()) {
                    int assignedWithRoom = assigned[i - 1][j - 1] + 1;
                    int wasteWithRoom = waste[i - 1][j - 1] + room.getCapacity() - course.getEnrolledStudents();
                    if (better(assignedWithRoom, wasteWithRoom, assigned[i][j], waste[i][j])) {
                        assigned[i][j] = assignedWithRoom;
                        waste[i][j] = wasteWithRoom;
                        action[i][j] = 3;
                    }
                }
            }
        }

        List<ScheduleEntry> assignments = new ArrayList<>();
        int i = courseCount;
        int j = roomCount;
        while (i > 0 && j > 0) {
            if (action[i][j] == 1) {
                i--;
            } else if (action[i][j] == 2) {
                j--;
            } else {
                Course course = sortedCourses.get(i - 1);
                Room room = sortedRooms.get(j - 1);
                assignments.add(new ScheduleEntry(course, room, slot,
                        room.getCapacity() - course.getEnrolledStudents()));
                i--;
                j--;
            }
        }
        return assignments;
    }

    private boolean better(int candidateCount, int candidateWaste, int currentCount, int currentWaste) {
        return candidateCount > currentCount || candidateCount == currentCount && candidateWaste < currentWaste;
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
            Set<String> scheduledIds = new HashSet<>();
            for (ScheduleEntry entry : entries) {
                result.addScheduledEntry(entry);
                scheduledIds.add(entry.getCourse().getId());
            }
            for (Course course : coursesForSlot) {
                if (!scheduledIds.contains(course.getId())) result.addUnscheduledCourse(course);
            }
        }
        for (Course course : data.getClasses()) if (!assignments.containsKey(course.getId())) result.addUnscheduledCourse(course);
        return result;
    }
}
