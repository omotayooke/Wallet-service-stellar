package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		System.setProperty("https.proxyHost", "trendgate.interswitchng.com");
		System.setProperty("https.proxyPort", "8080");
		SpringApplication.run(DemoApplication.class, args);
	}
}
