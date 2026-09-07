package com.campusscheduler.service;

import com.campusscheduler.model.ClassOccurrence;
import com.campusscheduler.model.Course;

import java.util.ArrayList;
import java.util.List;

public class ClassOccurrenceGenerator {

    public List<ClassOccurrence> generate(
            List<Course> courses
    ) {

        List<ClassOccurrence> occurrences = new ArrayList<>();

        for (Course course : courses) {

            for (int number = 1;
                 number <= course.getClassesPerWeek();
                 number++) {

                String occurrenceId = course.getId()
                                + "_"
                                + number;

                ClassOccurrence occurrence = new ClassOccurrence(
                                occurrenceId,
                                course,
                                number
                        );

                occurrences.add(occurrence);
            }
        }

        return occurrences;
    }
}