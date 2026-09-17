package com.campusscheduler.api;

import com.campusscheduler.model.ScheduleResult;
import com.campusscheduler.service.SchedulingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final SchedulingService scheduling;
    public ScheduleController(SchedulingService scheduling) { this.scheduling = scheduling; }
    @GetMapping
    public ScheduleResult generate(@RequestParam(defaultValue = "backtracking") String algorithm) {
        return scheduling.generate(algorithm);
    }
}
