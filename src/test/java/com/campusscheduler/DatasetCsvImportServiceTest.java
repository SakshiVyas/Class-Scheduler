package com.campusscheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.campusscheduler.service.DatasetCsvImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DatasetCsvImportServiceTest {
    @Autowired
    private DatasetCsvImportService importer;

    @Test
    void importsAllFourDatasetFiles() throws Exception {
        var result = importer.importDataset(
                file("professors", "id,name\nP1,Professor One\nP2,Professor Two\n"),
                file("courses", "id,name,professor_id,enrolled_students\nC1,Algorithms,P1,40\nC2,Databases,P2,30\n"),
                file("rooms", "id,capacity\nR1,40\nR2,60\n"),
                file("students", "id,name,group_id,course_ids\nS1,Ada,G1,C1;C2\nS2,Grace,G1,C1\n"));

        assertEquals(2, result.professors());
        assertEquals(2, result.courses());
        assertEquals(2, result.rooms());
        assertEquals(2, result.students());
    }

    private MockMultipartFile file(String name, String contents) {
        return new MockMultipartFile(name, name + ".csv", "text/csv", contents.getBytes());
    }
}
