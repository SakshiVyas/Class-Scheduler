package com.campusscheduler;

import com.campusscheduler.demo.AlgorithmDemo;
import com.campusscheduler.model.ConstraintData;
import com.campusscheduler.service.TimeSlotGenerator;
import com.campusscheduler.service.ConstraintDataLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        try {
            ConstraintData data = new ConstraintDataLoader().load("data/constraints.json");
            System.out.println("\n===== DATA LOADED =====");
            System.out.println("Courses: " + data.getClasses().size());
            System.out.println("Rooms: " + data.getRooms().size());
            System.out.println("Time slots: " + new TimeSlotGenerator().generate(data.getScheduleSettings()).size());
            new AlgorithmDemo().runAllDemos(data);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
