package com.campusscheduler.api.dto;

import java.util.List;

public record ScheduleComparison(int courseCount, int roomCount, int timeSlotCount, List<AlgorithmRun> algorithms) {}
