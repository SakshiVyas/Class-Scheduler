package com.campusscheduler.service;

import com.campusscheduler.persistence.StudentEntity;
import com.campusscheduler.persistence.StudentRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StudentCsvImportService {
    private final StudentRepository students;
    public StudentCsvImportService(StudentRepository students) { this.students = students; }

    public int importCsv(MultipartFile file) throws IOException {
        int imported = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line; boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.isBlank()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length < 3) throw new IllegalArgumentException("CSV requires student_id,student_name,group_id[,course_ids]");
                String courseIds = fields.length > 3 ? fields[3].trim() : "";
                students.save(new StudentEntity(fields[0].trim(), fields[1].trim(), fields[2].trim(), courseIds));
                imported++;
            }
        }
        return imported;
    }
}
