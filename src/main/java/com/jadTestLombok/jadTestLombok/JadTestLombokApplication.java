package com.jadTestLombok.jadTestLombok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JadTestLombokApplication {

	public static void main(String[] args) {
		SpringApplication.run(JadTestLombokApplication.class, args);
		student student1 = new student(1,"Jad");
		System.out.println(student1.getId());
		System.out.println(student1.getName());
	}

}
