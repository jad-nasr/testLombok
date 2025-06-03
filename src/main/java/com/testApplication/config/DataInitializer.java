package com.testApplication.config;

import com.testApplication.model.Role;
import com.testApplication.model.enums.RoleEnum; // Import your RoleEnum
import com.testApplication.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Option 1: Add as a bean in an existing @Configuration class (e.g., SecurityConfig or main app class)
// @Bean // If in SecurityConfig or main Application class
// CommandLineRunner initRoles(RoleRepository roleRepository) { ... }


// Option 2: Create a dedicated component for this
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            System.out.println("Initializing roles...");
            for (RoleEnum roleEnum : RoleEnum.values()) {
                String roleName = roleEnum.getAuthority(); // e.g., "ROLE_USER", "ROLE_ADMIN"
                if (roleRepository.findByName(roleName).isEmpty()) {
                    roleRepository.save(new Role(roleName));
                    System.out.println("Created role: " + roleName);
                }
            }
            System.out.println("Roles initialized.");
        };
    }
}