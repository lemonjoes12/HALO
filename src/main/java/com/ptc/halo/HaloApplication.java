package com.ptc.halo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HaloApplication {

	public static void main(String[] args) {

		// Check if GEMINI_API_KEY is available
		String geminiApiKey = System.getenv("GEMINI_API_KEY");

		if (geminiApiKey != null && !geminiApiKey.isBlank()) {
			System.out.println("GEMINI_API_KEY loaded successfully.");
		} else {
			System.out.println("WARNING: GEMINI_API_KEY is not configured.");
		}

		SpringApplication.run(HaloApplication.class, args);
	}
}