package com.campusscheduler.service;

import com.campusscheduler.persistence.CourseRepository;
import com.campusscheduler.persistence.ProfessorRepository;
import com.campusscheduler.persistence.RoomRepository;
import com.campusscheduler.persistence.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class DemoDataInitializer implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final DatasetCsvImportService importer;
    private final ProfessorRepository professors;
    private final CourseRepository courses;
    private final RoomRepository rooms;
    private final StudentRepository students;

    public DemoDataInitializer(DatasetCsvImportService importer, ProfessorRepository professors,
                               CourseRepository courses, RoomRepository rooms, StudentRepository students) {
        this.importer = importer;
        this.professors = professors;
        this.courses = courses;
        this.rooms = rooms;
        this.students = students;
    }

    @Override
    public void run(ApplicationArguments arguments) throws Exception {
        if (professors.count() > 0 || courses.count() > 0 || rooms.count() > 0 || students.count() > 0) {
            logger.info("Demo import skipped because the database already contains scheduling data");
            return;
        }

        DatasetCsvImportService.ImportSummary summary = importer.importDataset(
                new ClassPathResource("demo/professors_test.csv"),
                new ClassPathResource("demo/courses_test.csv"),
                new ClassPathResource("demo/rooms_test.csv"),
                new ClassPathResource("demo/students_test.csv")
        );
        logger.info("Demo data imported: {} professors, {} courses, {} rooms and {} students",
                summary.professors(), summary.courses(), summary.rooms(), summary.students());
    }
}
