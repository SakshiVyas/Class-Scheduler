package com.campusscheduler.service;
import com.campusscheduler.model.ConstraintData;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ConstraintDataLoader {

    private final ObjectMapper objectMapper;

    public ConstraintDataLoader() {
        this.objectMapper = new ObjectMapper();
    }

    public ConstraintData load(String filePath) throws IOException {

        return objectMapper.readValue(
                new File(filePath),
                ConstraintData.class
        );
    }
}
