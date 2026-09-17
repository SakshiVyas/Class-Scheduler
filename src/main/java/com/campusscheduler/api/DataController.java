package com.campusscheduler.api;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.service.DatabaseConstraintService;
import com.campusscheduler.service.StudentCsvImportService;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/data")
public class DataController {
    private final StudentCsvImportService importer;
    private final DatabaseConstraintService constraints;
    public DataController(StudentCsvImportService importer, DatabaseConstraintService constraints) { this.importer=importer; this.constraints=constraints; }

    @PostMapping(value="/students/import", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String,Integer> importStudents(@RequestPart("file") MultipartFile file) throws IOException {
        return Map.of("imported", importer.importCsv(file));
    }

    @GetMapping("/constraints")
    public ConstraintData getConstraints() { return constraints.loadConstraints(); }
}
