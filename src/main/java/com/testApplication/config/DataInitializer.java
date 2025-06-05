package com.testApplication.config;

import com.testApplication.model.enums.AccountTypeEnum;
import com.testApplication.model.enums.LegalEntityTypeEnum;
import com.testApplication.model.enums.RoleEnum; // Import your RoleEnum
import com.testApplication.repository.AccountTypeRepository;
import com.testApplication.repository.LegalEntityTypeRepository;
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
                    roleRepository.save(new com.testApplication.model.Role(roleName));
                    System.out.println("Created role: " + roleName);
                }
            }
            System.out.println("Roles initialized.");
        };
    }
    @Bean
    CommandLineRunner initAccountTypes(AccountTypeRepository accountTypeRepository) {
        return args -> {
            System.out.println("Initializing account types...");
            for (AccountTypeEnum accountTypeEnum : AccountTypeEnum.values()) {
                String accountTypeCode = accountTypeEnum.getAccountTypeCode();
                if (accountTypeRepository.findByCode(accountTypeCode).isEmpty()) {
                    accountTypeRepository.save(new com.testApplication.model.AccountType(accountTypeCode));
                    System.out.println("Created account type: " + accountTypeCode);
                }
            }
            System.out.println("Account types initialized.");
        };
    }
    @Bean
    CommandLineRunner initLegalEntityTypes(LegalEntityTypeRepository legalEntityTypeRepository) {
        return args -> {
            System.out.println("Initializing legal entity types...");
            for (LegalEntityTypeEnum legalEntityTypeEnum : LegalEntityTypeEnum.values()) {
                String legalEntityTypeCode = legalEntityTypeEnum.getLegalEntityTypeCode();
                if (legalEntityTypeRepository.findByCode(legalEntityTypeCode).isEmpty()) {
                    legalEntityTypeRepository.save(new com.testApplication.model.LegalEntityType(legalEntityTypeCode));
                    System.out.println("Created legal entity type: " + legalEntityTypeCode);
                }
            }
            System.out.println("Legal entity types initialized.");
        };
    }
}