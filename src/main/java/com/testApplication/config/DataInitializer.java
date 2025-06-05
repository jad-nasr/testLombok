package com.testApplication.config;

import com.testApplication.model.enums.LegalEntityTypeEnum;
import com.testApplication.model.enums.RoleEnum;
import com.testApplication.model.enums.AccountTypeEnum;
import com.testApplication.model.enums.AccountCategoryEnum;
import com.testApplication.model.AccountCategory;
import com.testApplication.model.AccountType;
import com.testApplication.repository.AccountTypeRepository;
import com.testApplication.repository.AccountCategoryRepository;
import com.testApplication.repository.LegalEntityTypeRepository;
import com.testApplication.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class DataInitializer {

    @Bean
    @Order(1)
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            System.out.println("Initializing roles...");
            for (RoleEnum roleEnum : RoleEnum.values()) {
                String roleName = roleEnum.getAuthority();
                if (roleRepository.findByName(roleName).isEmpty()) {
                    roleRepository.save(new com.testApplication.model.Role(roleName));
                    System.out.println("Created role: " + roleName);
                }
            }
            System.out.println("Roles initialized.");
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner initAccountCategories(AccountCategoryRepository accountCategoryRepository) {
        return args -> {
            System.out.println("Initializing account categories...");
            
            for (AccountCategoryEnum categoryEnum : AccountCategoryEnum.values()) {
                String code = categoryEnum.name();
                if (accountCategoryRepository.findByCode(code).isEmpty()) {
                    AccountCategory accountCategory = AccountCategory.builder()
                            .code(code)
                            .name(categoryEnum.getName())
                            .description(categoryEnum.getDescription())
                            .build();
                    accountCategoryRepository.save(accountCategory);
                    System.out.println("Created account category: " + code);
                }
            }
            System.out.println("Account categories initialized.");
        };
    }

    @Bean
    @Order(3)
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

    @Bean
    @Order(4)
    CommandLineRunner initAccountTypes(AccountTypeRepository accountTypeRepository, 
                                     AccountCategoryRepository accountCategoryRepository) {
        return args -> {
            System.out.println("Initializing account types...");
            
            for (AccountTypeEnum typeEnum : AccountTypeEnum.values()) {
                String code = typeEnum.name();
                if (accountTypeRepository.findByCode(code).isEmpty()) {
                    // Find the category
                    AccountCategory category = accountCategoryRepository.findByCode(typeEnum.getCategoryCode())
                            .orElseThrow(() -> new RuntimeException("Category not found: " + typeEnum.getCategoryCode()));
                            
                    AccountType accountType = AccountType.builder()
                            .code(code)
                            .name(typeEnum.getName())
                            .description(typeEnum.getDescription())
                            .accountCategory(category)
                            .build();
                            
                    accountTypeRepository.save(accountType);
                    System.out.println("Created account type: " + code + " in category: " + category.getCode());
                }
            }
            System.out.println("Account types initialized.");
        };
    }
}