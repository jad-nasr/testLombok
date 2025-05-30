package com.testApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class testApplication {

	@GetMapping("/messsaggess")
	public String getMessage() {
		return "asgdkjasd";
	}
	public static void main(String[] args) {
		SpringApplication.run(testApplication.class, args);
	}

}
