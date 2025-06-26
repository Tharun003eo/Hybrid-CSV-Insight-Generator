package com.insightiq.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.IOException;

/**
 * Main entry point for the InsightIQ Spring Boot application.
 */
@SpringBootApplication
public class BackendApplication {

	private Process pythonProcess;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		System.out.println("✅ InsightIQ Backend is running...");
	}

	/**
	 * Automatically launches the Python Flask microservice and opens the front-end in browser.
	 */
	@PostConstruct
	public void initialize() {
		startPythonService();
		openFrontendInBrowser();
	}

	/**
	 * Starts the Python microservice in the background.
	 */
	private void startPythonService() {
		try {
			String pythonExecutable = "python"; // or full path if required
			String scriptPath = "D:\\InsightIQ\\microservice\\analyze_service.py";

			ProcessBuilder pb = new ProcessBuilder(pythonExecutable, scriptPath);
			pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
			pb.redirectError(ProcessBuilder.Redirect.DISCARD);
			pythonProcess = pb.start();

			System.out.println("🚀 Python microservice launched at http://localhost:5000");
		} catch (IOException e) {
			System.err.println("❌ Failed to start Python service: " + e.getMessage());
		}
	}

	/**
	 * Opens the front-end index.html using the default browser via HTTP.
	 */
	private void openFrontendInBrowser() {
		try {
			String url = "http://localhost:8080/index.html"; // Served by Spring Boot static
			// For Windows & default browser
			String command = "cmd /c start " + url;
			Runtime.getRuntime().exec(command);
			System.out.println("🌐 Frontend opened at " + url);
		} catch (Exception e) {
			System.err.println("⚠️ Could not open browser: " + e.getMessage());
		}
	}

	/**
	 * Gracefully shuts down the Python process when Java backend exits.
	 */
	@PreDestroy
	public void stopPythonService() {
		if (pythonProcess != null && pythonProcess.isAlive()) {
			pythonProcess.destroy();
			System.out.println("🛑 Python microservice stopped.");
		}
	}
}

