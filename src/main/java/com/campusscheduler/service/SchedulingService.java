package com.campusscheduler.service;

import com.campusscheduler.algorithm.BacktrackingSolver;
import com.campusscheduler.algorithm.GreedySolver;
import com.campusscheduler.model.ScheduleResult;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {
    private final DatabaseConstraintService constraints;
    private final BacktrackingSolver backtracking;
    private final GreedySolver greedy;

    public SchedulingService(DatabaseConstraintService constraints, BacktrackingSolver backtracking, GreedySolver greedy) {
        this.constraints = constraints; this.backtracking = backtracking; this.greedy = greedy;
    }

    public ScheduleResult generate(String algorithm) {
        var data = constraints.loadConstraints();
        if ("greedy".equalsIgnoreCase(algorithm)) return greedy.solve(data);
        if (algorithm == null || "backtracking".equalsIgnoreCase(algorithm)) return backtracking.solve(data);
        throw new IllegalArgumentException("Unsupported algorithm: " + algorithm + ". Use backtracking or greedy.");
    }
}
