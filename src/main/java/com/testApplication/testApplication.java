package com.testApplication;

import com.testApplication.model.Role;
import com.testApplication.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

//	@Bean
//	CommandLineRunner initRoles(RoleRepository roleRepository) {
//		return args -> {
//			if (roleRepository.findByName("ROLE_USER").isEmpty()) {
//				roleRepository.save(new Role("ROLE_USER"));
//				System.out.println("Created ROLE_USER");
//			}
//			if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
//				roleRepository.save(new Role("ROLE_ADMIN"));
//				System.out.println("Created ROLE_ADMIN");
//			}
//			// Add other default roles as needed
//		};
//	}
}

