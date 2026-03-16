package com.butlert.nasa_neo_project;

import com.butlert.nasa_neo_project.service.NeoIngestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Scanner;

@SpringBootApplication
public class NasaNeoProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(NasaNeoProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(NeoIngestionService neoIngestionService) {
		return args -> {
			Scanner scanner = new Scanner(System.in);

			while (true) {
				System.out.print("\nRun daily ingestion? (y/n): ");
				String runDaily = scanner.nextLine().trim().toLowerCase();

				if (!runDaily.equals("y")) {
					System.out.println("Goodbye.");
					break;
				}

				System.out.print("Enter date to ingest (YYYY-MM-DD) or press Enter for today: ");
				String dateInput = scanner.nextLine().trim();

				LocalDate runDate;
				try {
					runDate = dateInput.isEmpty() ? LocalDate.now() : LocalDate.parse(dateInput);
				} catch (Exception ex) {
					System.out.println("Invalid date format. Please use YYYY-MM-DD.");
					continue;
				}

				System.out.println("Starting ingestion for date: " + runDate);

				try {
					neoIngestionService.runDaily(runDate);
					System.out.println("Ingestion completed for: " + runDate);
				} catch (Exception ex) {
					System.out.println("Ingestion failed for: " + runDate);
					System.out.println("Reason: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
				}
			}
		};
	}

}