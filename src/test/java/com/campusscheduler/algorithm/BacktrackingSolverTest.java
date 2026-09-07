package com.campusscheduler.algorithm;

import com.campusscheduler.model.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BacktrackingSolverTest {


    @Test
    public void shouldReturnBestEffortWhenOneOccurrenceCannotBeScheduled() {

        Course course1 =
                createCourse(
                        "C1",
                        20,
                        "P1",
                        "GROUP_1"
                );

        Course course2 =
                createCourse(
                        "C2",
                        50,
                        "P2",
                        "GROUP_2"
                );

        Course impossibleCourse =
                createCourse(
                        "IMPOSSIBLE",
                        1000,
                        "P3",
                        "GROUP_3"
                );


        ConstraintData data =
                new ConstraintData();

        data.setClasses(
                List.of(
                        course1,
                        course2,
                        impossibleCourse
                )
        );

        data.setRooms(
                List.of(
                        new Room("R1", 30),
                        new Room("R2", 60)
                )
        );

        data.setScheduleSettings(
                createScheduleSettings()
        );


        BacktrackingSolver solver =
                new BacktrackingSolver();

        ScheduleResult result =
                solver.solve(data);


        assertEquals(
                2,
                result.getScheduledEntries().size()
        );

        assertEquals(
                1,
                result.getUnscheduledOccurrences().size()
        );

        assertEquals(
                "IMPOSSIBLE_1",
                result.getUnscheduledOccurrences()
                        .get(0)
                        .getId()
        );
    }


    @Test
    public void shouldPreventProfessorDoubleBooking() {

        Course course1 =
                createCourse(
                        "C1",
                        20,
                        "P1",
                        "GROUP_1"
                );

        Course course2 =
                createCourse(
                        "C2",
                        20,
                        "P1",
                        "GROUP_2"
                );


        ConstraintData data =
                new ConstraintData();

        data.setClasses(
                List.of(
                        course1,
                        course2
                )
        );

        data.setRooms(
                List.of(
                        new Room("R1", 30),
                        new Room("R2", 30)
                )
        );

        data.setScheduleSettings(
                createScheduleSettings()
        );


        BacktrackingSolver solver =
                new BacktrackingSolver();

        ScheduleResult result =
                solver.solve(data);


        assertEquals(
                2,
                result.getScheduledEntries().size()
        );
    }


    private Course createCourse(
            String id,
            int enrolledStudents,
            String professorId,
            String studentGroup
    ) {

        Course course =
                new Course();

        course.setId(id);
        course.setEnrolledStudents(enrolledStudents);
        course.setProfessorId(professorId);

        course.setStudentGroups(
                List.of(studentGroup)
        );

        course.setDurationMinutes(60);
        course.setClassesPerWeek(1);

        return course;
    }


    private ScheduleSettings createScheduleSettings() {

        ScheduleSettings settings =
                new ScheduleSettings();

        settings.setWorkingDays(
                List.of("Monday")
        );

        settings.setDayStartTime("09:00");
        settings.setDayEndTime("17:00");
        settings.setSlotMinutes(30);

        return settings;
    }

    @Test
    public void shouldPreventOverlappingClassesForSameProfessor() {

        Course course1 =
                createCourse(
                        "C1",
                        20,
                        "P1",
                        "GROUP_1"
                );

        Course course2 =
                createCourse(
                        "C2",
                        20,
                        "P1",
                        "GROUP_2"
                );

        course1.setDurationMinutes(90);
        course2.setDurationMinutes(90);


        ConstraintData data =
                new ConstraintData();

        data.setClasses(
                List.of(
                        course1,
                        course2
                )
        );

        data.setRooms(
                List.of(
                        new Room("R1", 30),
                        new Room("R2", 30)
                )
        );


        ScheduleSettings settings =
                new ScheduleSettings();

        settings.setWorkingDays(
                List.of("Monday")
        );

        settings.setDayStartTime("09:00");
        settings.setDayEndTime("10:30");
        settings.setSlotMinutes(30);

        data.setScheduleSettings(settings);


        BacktrackingSolver solver =
                new BacktrackingSolver();

        ScheduleResult result =
                solver.solve(data);


        assertEquals(
                1,
                result.getScheduledEntries().size()
        );

        assertEquals(
                1,
                result.getUnscheduledOccurrences().size()
        );
    }

    @Test
    public void shouldSpreadRepeatedClassesAcrossDifferentDaysWhenPossible() {

        Course course =
                createCourse(
                        "CS101",
                        20,
                        "P1",
                        "GROUP_1"
                );

        course.setClassesPerWeek(2);
        course.setDurationMinutes(60);


        ConstraintData data =
                new ConstraintData();

        data.setClasses(
                List.of(course)
        );

        data.setRooms(
                List.of(
                        new Room("R1", 30)
                )
        );


        ScheduleSettings settings =
                new ScheduleSettings();

        settings.setWorkingDays(
                List.of(
                        "Monday",
                        "Tuesday"
                )
        );

        settings.setDayStartTime("09:00");
        settings.setDayEndTime("11:00");
        settings.setSlotMinutes(30);

        data.setScheduleSettings(settings);


        BacktrackingSolver solver =
                new BacktrackingSolver();

        ScheduleResult result =
                solver.solve(data);


        assertEquals(
                2,
                result.getScheduledEntries().size()
        );

        assertEquals(
                0,
                result.getUnscheduledOccurrences().size()
        );


        String firstDay =
                result.getScheduledEntries()
                        .get(0)
                        .getTimeSlot()
                        .getDay();

        String secondDay =
                result.getScheduledEntries()
                        .get(1)
                        .getTimeSlot()
                        .getDay();


        assertNotEquals(
                firstDay,
                secondDay
        );
    }

    @Test
    public void shouldPreferScheduleWithLowerRoomWaste() {

        Course course =
                createCourse(
                        "CS101",
                        25,
                        "P1",
                        "GROUP_1"
                );

        course.setClassesPerWeek(1);
        course.setDurationMinutes(60);


        ConstraintData data =
                new ConstraintData();

        data.setClasses(
                List.of(course)
        );

        data.setRooms(
                List.of(
                        new Room("R_SMALL", 30),
                        new Room("R_LARGE", 100)
                )
        );


        ScheduleSettings settings =
                new ScheduleSettings();

        settings.setWorkingDays(
                List.of("Monday")
        );

        settings.setDayStartTime("09:00");
        settings.setDayEndTime("10:00");
        settings.setSlotMinutes(30);

        data.setScheduleSettings(settings);


        BacktrackingSolver solver =
                new BacktrackingSolver();

        ScheduleResult result =
                solver.solve(data);


        assertEquals(
                1,
                result.getScheduledEntries().size()
        );

        assertEquals(
                0,
                result.getUnscheduledOccurrences().size()
        );


        ScheduleEntry entry =
                result.getScheduledEntries()
                        .get(0);


        assertEquals(
                "R_SMALL",
                entry.getRoom().getId()
        );

        assertEquals(
                5,
                entry.getWastedSeats()
        );

        assertEquals(
                5,
                result.getTotalWastedSeats()
        );
    }
}