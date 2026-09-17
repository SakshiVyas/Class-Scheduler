package com.campusscheduler.service;

import com.campusscheduler.persistence.CourseEntity;
import com.campusscheduler.persistence.CourseRepository;
import com.campusscheduler.persistence.ProfessorEntity;
import com.campusscheduler.persistence.ProfessorRepository;
import com.campusscheduler.persistence.RoomEntity;
import com.campusscheduler.persistence.RoomRepository;
import com.campusscheduler.persistence.StudentEntity;
import com.campusscheduler.persistence.StudentRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DatasetCsvImportService {
    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    public DatasetCsvImportService(ProfessorRepository professorRepository, CourseRepository courseRepository,
                                   RoomRepository roomRepository, StudentRepository studentRepository) {
        this.professorRepository = professorRepository;
        this.courseRepository = courseRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional
    public ImportSummary importDataset(MultipartFile professorFile, MultipartFile courseFile,
                                       MultipartFile roomFile, MultipartFile studentFile) throws IOException {
        List<ProfessorEntity> professorRows = parseProfessors(professorFile);
        List<CourseEntity> courseRows = parseCourses(courseFile);
        List<RoomEntity> roomRows = parseRooms(roomFile);
        List<StudentEntity> studentRows = parseStudents(studentFile);
        validate(professorRows, courseRows, roomRows, studentRows);

        studentRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
        professorRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        professorRepository.saveAll(professorRows);
        roomRepository.saveAll(roomRows);
        courseRepository.saveAll(courseRows);
        studentRepository.saveAll(studentRows);
        return new ImportSummary(professorRows.size(), courseRows.size(), roomRows.size(), studentRows.size());
    }

    private List<ProfessorEntity> parseProfessors(MultipartFile file) throws IOException {
        List<ProfessorEntity> result = new ArrayList<>();
        for (String[] row : rows(file, "professors", 2)) result.add(new ProfessorEntity(value(row, 0), value(row, 1)));
        return result;
    }

    private List<CourseEntity> parseCourses(MultipartFile file) throws IOException {
        List<CourseEntity> result = new ArrayList<>();
        for (String[] row : rows(file, "courses", 4)) {
            result.add(new CourseEntity(value(row, 0), value(row, 1), value(row, 2), positiveInteger(row, 3, "enrolled_students")));
        }
        return result;
    }

    private List<RoomEntity> parseRooms(MultipartFile file) throws IOException {
        List<RoomEntity> result = new ArrayList<>();
        for (String[] row : rows(file, "rooms", 2)) result.add(new RoomEntity(value(row, 0), positiveInteger(row, 1, "capacity")));
        return result;
    }

    private List<StudentEntity> parseStudents(MultipartFile file) throws IOException {
        List<StudentEntity> result = new ArrayList<>();
        for (String[] row : rows(file, "students", 4)) {
            result.add(new StudentEntity(value(row, 0), value(row, 1), value(row, 2), value(row, 3)));
        }
        return result;
    }

    private List<String[]> rows(MultipartFile file, String label, int columns) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Choose a " + label + " CSV file.");
        List<String[]> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                String[] row = line.split(",", -1);
                if (row.length != columns) throw new IllegalArgumentException(label + " CSV line " + lineNumber
                        + " must contain " + columns + " columns.");
                result.add(row);
            }
        }
        if (result.isEmpty()) throw new IllegalArgumentException(label + " CSV contains no data rows.");
        return result;
    }

    private String value(String[] row, int index) {
        String value = row[index].trim();
        if (value.isEmpty()) throw new IllegalArgumentException("CSV fields cannot be blank.");
        return value;
    }

    private int positiveInteger(String[] row, int index, String field) {
        try {
            int value = Integer.parseInt(value(row, index));
            if (value <= 0) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(field + " must be a positive whole number.");
        }
    }

    private void validate(List<ProfessorEntity> professorRows, List<CourseEntity> courseRows,
                          List<RoomEntity> roomRows, List<StudentEntity> studentRows) {
        Set<String> professorIds = unique(professorRows.stream().map(ProfessorEntity::getId).toList(), "professor");
        Set<String> courseIds = unique(courseRows.stream().map(CourseEntity::getId).toList(), "course");
        unique(roomRows.stream().map(RoomEntity::getId).toList(), "room");
        unique(studentRows.stream().map(StudentEntity::getId).toList(), "student");
        if (courseRows.stream().anyMatch(course -> !professorIds.contains(course.getProfessorId()))) {
            throw new IllegalArgumentException("Every course must reference a professor in professors.csv.");
        }
        for (StudentEntity student : studentRows) {
            for (String courseId : student.getCourseIds().split(";")) {
                if (!courseIds.contains(courseId.trim())) {
                    throw new IllegalArgumentException("Student course_ids must reference courses in courses.csv.");
                }
            }
        }
    }

    private Set<String> unique(List<String> ids, String label) {
        Set<String> uniqueIds = new HashSet<>(ids);
        if (uniqueIds.size() != ids.size()) throw new IllegalArgumentException("Duplicate " + label + " IDs are not allowed.");
        return uniqueIds;
    }

    public record ImportSummary(int professors, int courses, int rooms, int students) {}
}
