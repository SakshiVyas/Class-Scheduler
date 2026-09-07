package com.campusscheduler.algorithm;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.ScheduleSettings;
import com.campusscheduler.model.TimeSlot;
import com.campusscheduler.service.ClassOccurrenceGenerator;

import com.campusscheduler.service.TimeOverlapChecker;
import com.campusscheduler.service.TimeSlotGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class WelshPowellSolverTest {


    @Test
    public void shouldGiveDifferentColorsToConflictingOccurrences() {

        Course course1 = new Course();
        course1.setId("C1");
        course1.setProfessorId("P1");
        course1.setEnrolledStudents(30);
        course1.setStudentGroups(List.of("GROUP_1"));
        course1.setDurationMinutes(60);
        course1.setClassesPerWeek(1);


        Course course2 = new Course();
        course2.setId("C2");
        course2.setProfessorId("P1");
        course2.setEnrolledStudents(25);
        course2.setStudentGroups(List.of("GROUP_2"));
        course2.setDurationMinutes(60);
        course2.setClassesPerWeek(1);


        Course course3 = new Course();
        course3.setId("C3");
        course3.setProfessorId("P3");
        course3.setEnrolledStudents(20);
        course3.setStudentGroups(List.of("GROUP_1"));
        course3.setDurationMinutes(60);
        course3.setClassesPerWeek(1);


        ClassOccurrenceGenerator generator =
                new ClassOccurrenceGenerator();

        List<ClassOccurrence> occurrences =
                generator.generate(
                        List.of(
                                course1,
                                course2,
                                course3
                        )
                );


        ConflictGraph graph =
                new ConflictGraph();

        graph.buildGraph(occurrences);


        WelshPowellSolver solver =
                new WelshPowellSolver();

        Map<String, Integer> colors =
                solver.colorGraph(graph);


        assertEquals(
                3,
                colors.size()
        );


        // C1 conflicts with C2 because they share professor P1.
        assertNotEquals(
                colors.get("C1_1"),
                colors.get("C2_1")
        );


        // C1 conflicts with C3 because they share GROUP_1.
        assertNotEquals(
                colors.get("C1_1"),
                colors.get("C3_1")
        );
    }


    @Test
    public void shouldSeparateRepeatedOccurrencesOfSameCourse() {

        Course course = new Course();
        course.setId("CS101");
        course.setProfessorId("P1");
        course.setEnrolledStudents(50);
        course.setStudentGroups(List.of("GROUP_1"));
        course.setDurationMinutes(60);
        course.setClassesPerWeek(2);


        ClassOccurrenceGenerator generator =
                new ClassOccurrenceGenerator();

        List<ClassOccurrence> occurrences =
                generator.generate(
                        List.of(course)
                );


        ConflictGraph graph =
                new ConflictGraph();

        graph.buildGraph(occurrences);


        WelshPowellSolver solver =
                new WelshPowellSolver();

        Map<String, Integer> colors =
                solver.colorGraph(graph);


        assertEquals(
                2,
                colors.size()
        );


        assertNotEquals(
                colors.get("CS101_1"),
                colors.get("CS101_2")
        );
    }

    @Test
    public void shouldAssignNonOverlappingTimesToConflictingOccurrences() {

        Course course1 = new Course();
        course1.setId("C1");
        course1.setProfessorId("P1");
        course1.setEnrolledStudents(30);
        course1.setStudentGroups(List.of("GROUP_1"));
        course1.setDurationMinutes(90);
        course1.setClassesPerWeek(1);


        Course course2 = new Course();
        course2.setId("C2");
        course2.setProfessorId("P1");
        course2.setEnrolledStudents(30);
        course2.setStudentGroups(List.of("GROUP_2"));
        course2.setDurationMinutes(90);
        course2.setClassesPerWeek(1);


        ClassOccurrenceGenerator occurrenceGenerator =
                new ClassOccurrenceGenerator();

        List<ClassOccurrence> occurrences =
                occurrenceGenerator.generate(
                        List.of(course1, course2)
                );


        ConflictGraph graph =
                new ConflictGraph();

        graph.buildGraph(occurrences);


        ScheduleSettings settings =
                new ScheduleSettings();

        settings.setWorkingDays(
                List.of("Monday")
        );

        settings.setDayStartTime("09:00");
        settings.setDayEndTime("12:00");
        settings.setSlotMinutes(30);


        TimeSlotGenerator timeSlotGenerator =
                new TimeSlotGenerator();

        List<TimeSlot> timeSlots =
                timeSlotGenerator.generate(settings);


        WelshPowellSolver solver =
                new WelshPowellSolver();

        Map<String, TimeSlot> assignments =
                solver.assignTimeSlots(
                        graph,
                        occurrences,
                        timeSlots,
                        settings
                );


        assertEquals(
                2,
                assignments.size()
        );


        TimeSlot firstTime =
                assignments.get("C1_1");

        TimeSlot secondTime =
                assignments.get("C2_1");


        TimeOverlapChecker overlapChecker =
                new TimeOverlapChecker();


        boolean overlaps =
                overlapChecker.overlaps(
                        occurrences.get(0),
                        firstTime,
                        occurrences.get(1),
                        secondTime
                );


        assertFalse(overlaps);
    }
}