package com.campusscheduler;

import com.campusscheduler.algorithm.*;
import com.campusscheduler.demo.AlgorithmDemo;
import com.campusscheduler.model.*;
import com.campusscheduler.service.ConstraintDataLoader;
import com.campusscheduler.service.TimeSlotGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);

		try {
			// loading input data
			ConstraintDataLoader loader = new ConstraintDataLoader();

			ConstraintData data =
					loader.load("data/constraints.json");

			printDataSummary(data);

            // demo

			AlgorithmDemo demoRunner = new AlgorithmDemo();

			demoRunner.runAllDemos(data);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printDataSummary(
			ConstraintData data
	) {

		TimeSlotGenerator timeSlotGenerator =
				new TimeSlotGenerator();

		List<TimeSlot> timeSlots =
				timeSlotGenerator.generate(
						data.getScheduleSettings()
				);

		System.out.println();
		System.out.println(
				"===== DATA LOADED ====="
		);

		System.out.println(
				"Courses: "
						+ data.getClasses().size()
		);

		System.out.println(
				"Rooms: "
						+ data.getRooms().size()
		);

		System.out.println(
				"Time Slots: "
						+ timeSlots.size()
		);

	}
}
