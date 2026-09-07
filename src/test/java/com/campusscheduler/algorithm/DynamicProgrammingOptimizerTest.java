package com.campusscheduler.algorithm;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.Course;
import com.campusscheduler.model.Room;
import com.campusscheduler.model.ScheduleEntry;
import com.campusscheduler.model.TimeSlot;
import com.campusscheduler.service.ClassOccurrenceGenerator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicProgrammingOptimizerTest {


    @Test
    public void shouldFindMinimumWasteForRoomAssignment() {

        Course course1 = createCourse(
                "C1",
                30,
                "P1"
        );

        Course course2 = createCourse(
                "C2",
                30,
                "P2"
        );

        Course course3 = createCourse(
                "C3",
                50,
                "P3"
        );


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


        List<Room> rooms =
                List.of(
                        new Room("R1", 30),
                        new Room("R2", 40),
                        new Room("R3", 50),
                        new Room("R4", 60)
                );


        TimeSlot timeSlot =
                new TimeSlot(
                        "MON_09",
                        "Monday",
                        "09:00"
                );


        DynamicProgrammingOptimizer optimizer =
                new DynamicProgrammingOptimizer();


        List<ScheduleEntry> assignments =
                optimizer.optimizeRoomsForTimeSlot(
                        occurrences,
                        rooms,
                        timeSlot
                );


        assertEquals(
                3,
                assignments.size()
        );


        int totalWaste = 0;

        for (ScheduleEntry entry : assignments) {
            totalWaste += entry.getWastedSeats();
        }


        assertEquals(
                10,
                totalWaste
        );
    }


    @Test
    public void shouldReturnNoAssignmentsWhenThereAreNotEnoughRooms() {

        Course course1 =
                createCourse(
                        "C1",
                        20,
                        "P1"
                );

        Course course2 =
                createCourse(
                        "C2",
                        20,
                        "P2"
                );

        Course course3 =
                createCourse(
                        "C3",
                        20,
                        "P3"
                );


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


        List<Room> rooms =
                List.of(
                        new Room("R1", 30),
                        new Room("R2", 30)
                );


        TimeSlot timeSlot =
                new TimeSlot(
                        "MON_09",
                        "Monday",
                        "09:00"
                );


        DynamicProgrammingOptimizer optimizer =
                new DynamicProgrammingOptimizer();


        List<ScheduleEntry> assignments =
                optimizer.optimizeRoomsForTimeSlot(
                        occurrences,
                        rooms,
                        timeSlot
                );


        assertEquals(
                0,
                assignments.size()
        );
    }


    private Course createCourse(
            String id,
            int enrolledStudents,
            String professorId
    ) {

        Course course =
                new Course();

        course.setId(id);
        course.setEnrolledStudents(enrolledStudents);
        course.setProfessorId(professorId);

        course.setStudentGroups(
                List.of(id + "_GROUP")
        );

        course.setDurationMinutes(60);
        course.setClassesPerWeek(1);

        return course;
    }
}