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
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BacktrackingSolver {
    private List<ScheduleEntry> bestSchedule = new ArrayList<>();
    private int bestWaste = Integer.MAX_VALUE;
    private List<TimeSlot> candidateSlots = List.of();
    private long deadlineNanos;
    private long nodesVisited;
    private static final long DEFAULT_MAX_MILLIS = 5_000;
    private static final long MAX_NODES = 1_000_000;

    private boolean sharesGroup(Course first, Course second, Map<String, List<String>> groups) {
        if (groups == null) return false;
        for (List<String> courses : groups.values()) if (courses != null && courses.contains(first.getId()) && courses.contains(second.getId())) return true;
        return false;
    }

    private boolean valid(Course course, Room room, TimeSlot slot, List<ScheduleEntry> schedule, Map<String, List<String>> groups) {
        if (room.getCapacity() < course.getEnrolledStudents()) return false;
        for (ScheduleEntry entry : schedule) {
            if (!entry.getTimeSlot().getId().equals(slot.getId())) continue;
            if (entry.getRoom().getId().equals(room.getId())) return false;
            if (entry.getCourse().getProfessorId().equals(course.getProfessorId())) return false;
            if (sharesGroup(entry.getCourse(), course, groups)) return false;
        }
        return true;
    }

    private void updateBest(List<ScheduleEntry> schedule) {
        int waste = schedule.stream().mapToInt(ScheduleEntry::getWastedSeats).sum();
        if (schedule.size() > bestSchedule.size() || (schedule.size() == bestSchedule.size() && waste < bestWaste)) {
            bestSchedule = new ArrayList<>(schedule);
            bestWaste = waste;
        }
    }

    private void search(int index, List<Course> courses, ConstraintData data, List<ScheduleEntry> current) {
        if (++nodesVisited > MAX_NODES || System.nanoTime() >= deadlineNanos) return;
        if (current.size() + courses.size() - index < bestSchedule.size()) return;
        if (index == courses.size()) { updateBest(current); return; }
        Course course = courses.get(index);
        List<Room> rooms = new ArrayList<>(data.getRooms());
        rooms.sort(Comparator.comparingInt(Room::getCapacity));
        for (TimeSlot slot : candidateSlots) for (Room room : rooms) {
            if (valid(course, room, slot, current, data.getStudentGroups())) {
                current.add(new ScheduleEntry(course, room, slot, room.getCapacity() - course.getEnrolledStudents()));
                search(index + 1, courses, data, current);
                current.remove(current.size() - 1);
            }
        }
        search(index + 1, courses, data, current);
    }

    public ScheduleResult solve(ConstraintData data) {
        bestSchedule = new ArrayList<>();
        bestWaste = Integer.MAX_VALUE;
        nodesVisited = 0;
        long maxMillis = Long.getLong("scheduler.backtracking.maxMillis", DEFAULT_MAX_MILLIS);
        deadlineNanos = System.nanoTime() + Math.max(1, maxMillis) * 1_000_000L;
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(data);
        List<Course> courses = sortCoursesByDifficulty(data);
        List<TimeSlot> generatedSlots = new TimeSlotGenerator().generate(data.getScheduleSettings());
        candidateSlots = generatedSlots.subList(0, Math.min(generatedSlots.size(), Math.max(1, courses.size())));
        search(0, courses, data, new ArrayList<>());
        ScheduleResult result = new ScheduleResult();
        for (ScheduleEntry entry : bestSchedule) result.addScheduledEntry(entry);
        for (Course course : data.getClasses()) {
            boolean scheduled = bestSchedule.stream().anyMatch(entry -> entry.getCourse().getId().equals(course.getId()));
            if (!scheduled) result.addUnscheduledCourse(course);
        }
        return result;
    }

    public List<Course> sortCoursesByDifficulty(ConstraintData data) {
        ConflictGraph graph = new ConflictGraph();
        graph.buildGraph(data);
        List<Course> courses = new ArrayList<>(data.getClasses());
        courses.sort(Comparator.comparingInt((Course c) -> graph.getConflicts(c.getId()).size()).reversed()
                .thenComparing(Comparator.comparingInt(Course::getEnrolledStudents).reversed()));
        return courses;
    }
}
