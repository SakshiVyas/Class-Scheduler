package com.campusscheduler.algorithm;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.Course;
import com.campusscheduler.service.ClassOccurrenceGenerator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConflictGraphTest {


    @Test
    public void shouldCreateConflictsForProfessorAndStudentGroup() {

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


        // C1 and C2 have the same professor.
        assertTrue(
                graph.getConflicts("C1_1")
                        .contains("C2_1")
        );


        // C1 and C3 share GROUP_1.
        assertTrue(
                graph.getConflicts("C1_1")
                        .contains("C3_1")
        );


        // C2 and C3 have different professors
        // and different student groups.
        assertFalse(
                graph.getConflicts("C2_1")
                        .contains("C3_1")
        );
    }


    @Test
    public void shouldPreventOccurrencesOfSameCourseFromOverlapping() {

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


        assertTrue(
                graph.getConflicts("CS101_1")
                        .contains("CS101_2")
        );

        assertTrue(
                graph.getConflicts("CS101_2")
                        .contains("CS101_1")
        );
    }
}