package com.campusscheduler.algorithm;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.service.ClassOccurrenceGenerator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreedySolverTest {


    @Test
    public void shouldSortLargestOccurrenceFirst() {

        Course smallCourse = new Course();
        smallCourse.setId("SMALL");
        smallCourse.setEnrolledStudents(20);
        smallCourse.setProfessorId("P1");
        smallCourse.setStudentGroups(List.of("GROUP_1"));
        smallCourse.setDurationMinutes(60);
        smallCourse.setClassesPerWeek(1);


        Course largeCourse = new Course();
        largeCourse.setId("LARGE");
        largeCourse.setEnrolledStudents(80);
        largeCourse.setProfessorId("P2");
        largeCourse.setStudentGroups(List.of("GROUP_2"));
        largeCourse.setDurationMinutes(60);
        largeCourse.setClassesPerWeek(1);


        ClassOccurrenceGenerator generator =
                new ClassOccurrenceGenerator();

        List<ClassOccurrence> occurrences =
                generator.generate(
                        List.of(
                                smallCourse,
                                largeCourse
                        )
                );


        GreedySolver solver =
                new GreedySolver();

        List<ClassOccurrence> sorted =
                solver.sortOccurrencesBySize(
                        occurrences
                );


        assertEquals(
                "LARGE",
                sorted.get(0)
                        .getCourse()
                        .getId()
        );
    }


    @Test
    public void shouldLeaveOccurrenceUnscheduledWhenRoomIsTooSmall() {

        Course impossibleCourse = new Course();
        impossibleCourse.setId("IMPOSSIBLE");
        impossibleCourse.setEnrolledStudents(1000);
        impossibleCourse.setProfessorId("P1");
        impossibleCourse.setStudentGroups(List.of("GROUP_1"));
        impossibleCourse.setDurationMinutes(60);
        impossibleCourse.setClassesPerWeek(1);


        ConstraintData data =
                new ConstraintData();

        data.setClasses(
                List.of(impossibleCourse)
        );

        data.setRooms(
                List.of(
                        new Room("R1", 100)
                )
        );

        data.setScheduleSettings(
                createScheduleSettings()
        );


        GreedySolver solver =
                new GreedySolver();

        ScheduleResult result =
                solver.solve(data);


        assertEquals(
                0,
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

        Course course1 = new Course();
        course1.setId("C1");
        course1.setEnrolledStudents(20);
        course1.setProfessorId("P1");
        course1.setStudentGroups(List.of("GROUP_1"));
        course1.setDurationMinutes(90);
        course1.setClassesPerWeek(1);


        Course course2 = new Course();
        course2.setId("C2");
        course2.setEnrolledStudents(20);
        course2.setProfessorId("P1");
        course2.setStudentGroups(List.of("GROUP_2"));
        course2.setDurationMinutes(90);
        course2.setClassesPerWeek(1);


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


        GreedySolver solver =
                new GreedySolver();

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
}