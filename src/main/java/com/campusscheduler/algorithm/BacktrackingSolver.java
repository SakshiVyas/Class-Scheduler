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
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BacktrackingSolver {
    private List<ScheduleEntry> bestSchedule = new ArrayList<>();
    private int bestWaste = Integer.MAX_VALUE;
    private List<TimeSlot> candidateSlots = List.of();
    private List<Room> candidateRooms = List.of();
    private int[] minimumRemainingWaste = new int[0];
    private int globalWasteLowerBound;
    private boolean optimalFound;
    private long deadlineNanos;
    private long nodesVisited;
    private boolean searchLimitReached;
    private static final long DEFAULT_MAX_MILLIS = 30_000;
    private static final long MAX_NODES = 1_000_000;

    private void updateBest(List<ScheduleEntry> schedule, int waste, int courseCount) {
        if (schedule.size() > bestSchedule.size() || (schedule.size() == bestSchedule.size() && waste < bestWaste)) {
            bestSchedule = new ArrayList<>(schedule);
            bestWaste = waste;
            optimalFound = schedule.size() == courseCount && waste == globalWasteLowerBound;
        }
    }

    private void search(int index, List<Course> courses, ConstraintData data, List<ScheduleEntry> current,
                        int currentWaste) {
        updateBest(current, currentWaste, courses.size());
        if (optimalFound) return;
        if (++nodesVisited > MAX_NODES || System.nanoTime() >= deadlineNanos) {
            searchLimitReached = true;
            return;
        }
        if (current.size() + courses.size() - index < bestSchedule.size()) return;
        if (bestSchedule.size() == courses.size()
                && currentWaste + minimumRemainingWaste[index] >= bestWaste) return;
        if (index == courses.size()) return;
        Course course = courses.get(index);
        for (Room room : candidateRooms) for (TimeSlot slot : candidateSlots) {
            if (ScheduleValidator.canPlace(course, room, slot, current, data.getStudentGroups())) {
                int waste = room.getCapacity() - course.getEnrolledStudents();
                current.add(new ScheduleEntry(course, room, slot, waste));
                search(index + 1, courses, data, current, currentWaste + waste);
                current.remove(current.size() - 1);
                if (optimalFound || searchLimitReached) return;
            }
        }
        if (!searchLimitReached) search(index + 1, courses, data, current, currentWaste);
    }

    public synchronized ScheduleResult solve(ConstraintData data) {
        bestSchedule = new ArrayList<>();
        bestWaste = Integer.MAX_VALUE;
        nodesVisited = 0;
        searchLimitReached = false;
        optimalFound = false;
        long maxMillis = Long.getLong("scheduler.backtracking.maxMillis", DEFAULT_MAX_MILLIS);
        deadlineNanos = System.nanoTime() + Math.max(1, maxMillis) * 1_000_000L;
        List<Course> courses = sortCoursesByDifficulty(data);
        List<TimeSlot> generatedSlots = new TimeSlotGenerator().generate(data.getScheduleSettings());
        candidateSlots = generatedSlots.subList(0, Math.min(generatedSlots.size(), Math.max(1, courses.size())));
        candidateRooms = new ArrayList<>(data.getRooms());
        candidateRooms.sort(Comparator.comparingInt(Room::getCapacity));
        minimumRemainingWaste = minimumRemainingWaste(courses, candidateRooms);
        globalWasteLowerBound = capacityWasteLowerBound(courses, candidateRooms, candidateSlots.size());
        search(0, courses, data, new ArrayList<>(), 0);
        ScheduleResult result = new ScheduleResult();
        for (ScheduleEntry entry : bestSchedule) result.addScheduledEntry(entry);
        for (Course course : data.getClasses()) {
            boolean scheduled = bestSchedule.stream().anyMatch(entry -> entry.getCourse().getId().equals(course.getId()));
            if (!scheduled) result.addUnscheduledCourse(course);
        }
        return result;
    }

    public boolean wasSearchLimitReached() {
        return searchLimitReached;
    }

    private int[] minimumRemainingWaste(List<Course> courses, List<Room> rooms) {
        int[] suffix = new int[courses.size() + 1];
        for (int i = courses.size() - 1; i >= 0; i--) {
            Course course = courses.get(i);
            int minimum = rooms.stream()
                    .filter(room -> room.getCapacity() >= course.getEnrolledStudents())
                    .mapToInt(room -> room.getCapacity() - course.getEnrolledStudents())
                    .min().orElse(0);
            suffix[i] = suffix[i + 1] + minimum;
        }
        return suffix;
    }

    private int capacityWasteLowerBound(List<Course> courses, List<Room> rooms, int slotCount) {
        if (courses.isEmpty()) return 0;
        if ((long) rooms.size() * slotCount < courses.size()) return Integer.MAX_VALUE;
        List<Integer> courseSizes = courses.stream().map(Course::getEnrolledStudents).sorted().toList();
        List<Integer> capacities = new ArrayList<>();
        for (Room room : rooms) {
            for (int i = 0; i < slotCount; i++) capacities.add(room.getCapacity());
        }
        capacities.sort(Integer::compareTo);
        int[][] dp = new int[courseSizes.size() + 1][capacities.size() + 1];
        int infinity = Integer.MAX_VALUE / 4;
        for (int i = 1; i <= courseSizes.size(); i++) dp[i][0] = infinity;
        for (int i = 1; i <= courseSizes.size(); i++) {
            for (int j = 1; j <= capacities.size(); j++) {
                dp[i][j] = dp[i][j - 1];
                int courseSize = courseSizes.get(i - 1);
                int capacity = capacities.get(j - 1);
                if (capacity >= courseSize && dp[i - 1][j - 1] != infinity) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 1][j - 1] + capacity - courseSize);
                }
            }
        }
        return dp[courseSizes.size()][capacities.size()];
    }

    public List<Course> sortCoursesByDifficulty(ConstraintData data) {
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(data);
        List<Course> courses = new ArrayList<>(data.getClasses());
        List<TimeSlot> slots = new TimeSlotGenerator().generate(data.getScheduleSettings());
        courses.sort(Comparator.comparingInt((Course c) -> feasiblePlacements(c, data.getRooms(), slots))
                .thenComparing(Comparator.comparingInt((Course c) -> graph.getConflicts(c.getId()).size()).reversed())
                .thenComparing(Comparator.comparingInt(Course::getEnrolledStudents).reversed()));
        return courses;
    }

    private int feasiblePlacements(Course course, List<Room> rooms, List<TimeSlot> slots) {
        int count = 0;
        for (Room room : rooms) if (room.getCapacity() >= course.getEnrolledStudents()) count += slots.size();
        return count;
    }
}
