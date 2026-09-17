package com.campusscheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ScaleDataConsistencyTest {
    @Test
    void scaleFixturesAreCompleteAndReferentiallyConsistent() throws IOException {
        Set<String> professorIds = ids("data/professors_test.csv");
        Set<String> courseIds = ids("data/courses_test.csv");
        Set<String> roomIds = ids("data/rooms_test.csv");
        Set<String> studentIds = ids("data/students_test.csv");

        assertEquals(300, professorIds.size());
        assertEquals(360, courseIds.size());
        assertEquals(50, roomIds.size());
        assertEquals(5_000, studentIds.size());

        Set<String> assignedProfessors;
        Map<String, Integer> declaredEnrolments = new HashMap<>();
        try (var lines = Files.lines(Path.of("data/courses_test.csv")).skip(1)) {
            var rows = lines.map(line -> line.split(",", -1)).toList();
            assignedProfessors = rows.stream().map(row -> row[2]).collect(Collectors.toSet());
            rows.forEach(row -> declaredEnrolments.put(row[0], Integer.parseInt(row[3])));
        }
        assertEquals(professorIds, assignedProfessors);

        Map<String, Integer> actualEnrolments = new HashMap<>();
        try (var lines = Files.lines(Path.of("data/students_test.csv")).skip(1)) {
            var enrolledCourseIds = lines.flatMap(line -> Arrays.stream(line.split(",", -1)[3].split(";"))).toList();
            assertTrue(enrolledCourseIds.stream().allMatch(courseIds::contains));
            enrolledCourseIds.forEach(courseId -> actualEnrolments.merge(courseId, 1, Integer::sum));
        }
        assertEquals(declaredEnrolments, actualEnrolments);
    }

    private Set<String> ids(String file) throws IOException {
        try (var lines = Files.lines(Path.of(file)).skip(1)) {
            return lines.map(line -> line.split(",", -1)[0]).collect(Collectors.toSet());
        }
    }
}
