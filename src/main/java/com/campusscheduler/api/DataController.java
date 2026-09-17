package com.campusscheduler.api;

import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.service.DatabaseConstraintService;
import com.campusscheduler.service.DatasetCsvImportService;
import java.io.IOException;
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
    private final DatasetCsvImportService datasetImporter;
    private final DatabaseConstraintService constraints;

    public DataController(DatasetCsvImportService datasetImporter, DatabaseConstraintService constraints) {
        this.datasetImporter = datasetImporter;
        this.constraints = constraints;
    }

    @PostMapping(value="/import", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public DatasetCsvImportService.ImportSummary importDataset(
            @RequestPart("professors") MultipartFile professors,
            @RequestPart("courses") MultipartFile courses,
            @RequestPart("rooms") MultipartFile rooms,
            @RequestPart("students") MultipartFile students) throws IOException {
        return datasetImporter.importDataset(professors, courses, rooms, students);
    }

    @GetMapping("/constraints")
    public ConstraintData getConstraints() {
        return constraints.loadConstraints();
    }
}
