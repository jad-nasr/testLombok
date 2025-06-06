package com.testApplication.util;

import com.testApplication.dto.*;
import com.testApplication.mapper.*;
import com.testApplication.model.*;
import com.testApplication.repository.*;
import com.testApplication.service.UserService;
import com.testApplication.service.AccountService;
import com.testApplication.service.LegalEntityService;
import com.testApplication.service.SecurityService;
import com.testApplication.service.UserEntityAccessService;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class SetupTestData {    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected LegalEntityTypeRepository legalEntityTypeRepository;    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UserEntityAccessRepository userEntityAccessRepository;

    @Autowired
    protected LegalEntityRepository legalEntityRepository;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected AccountTypeRepository accountTypeRepository;    @Autowired
    protected AccountMapper accountMapper;

    @MockitoBean
    protected LegalEntityMapper legalEntityMapper;

    protected LegalEntityType testLegalEntityType;
    protected UserDetails testUserDetails;
    protected User testUser;
    protected User testUser2;
    protected User testUnauthorizedUser;
    protected LegalEntity testLegalEntity;
    protected UserEntityAccess testUserAccess;
    protected Account testAccount;
    protected AccountType testAccountType;
    protected Account testParentAccount;
    protected SecurityContext testSecurityContext;
    protected Authentication testAuthentication;

    @Autowired
    private UserService userService;

    @Autowired
    private LegalEntityService legalEntityService;
    
    @Autowired
    private UserEntityAccessService userEntityAccessService;
    
    @Autowired
    private AccountService accountService;    @BeforeEach
    protected void setUpBase() {        // Create timestamp for unique identifiers
        String timestamp = String.valueOf(System.currentTimeMillis());

        // Get required roles from repository
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(userRole);
        adminRoles.add(adminRole);

        // Create primary test user with admin access
        testUser = userRepository.save(User.builder()
                .username("test-user-" + timestamp)
                .password("testPassword")
                .email("test" + timestamp + "@example.com")
                .roles(adminRoles)
                .build());

        // Set up Spring Security context with the test user
        testUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username(testUser.getUsername())
                .password(testUser.getPassword())
                .roles("USER", "ADMIN")
                .build();
        
        testAuthentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                testUserDetails, 
                null, 
                testUserDetails.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(testAuthentication);

        // Create additional test users with basic access
        testUser2 = userRepository.save(User.builder()
                .username("test-user2-" + timestamp)
                .email("test2-" + timestamp + "@example.com")
                .password("testPassword2")
                .roles(Set.of(userRole))
                .build());

        testUnauthorizedUser = userRepository.save(User.builder()
                .username("unauthorized-" + timestamp)
                .email("unauthorized-" + timestamp + "@example.com")
                .password("password")
                .roles(Set.of(userRole))
                .build());// Create and save test LegalEntityType
        testLegalEntityType = legalEntityTypeRepository.save(LegalEntityType.builder()
                .code("TEST_CORP_" + timestamp)
                .name("Test Corporation")
                .description("Test Legal Entity Type")
                .build());        testLegalEntity = legalEntityRepository.save(LegalEntity.builder()
                .name("Test Legal Entity " + timestamp)
                .legalEntityType(testLegalEntityType)
                .type(testLegalEntityType.getCode())  // Set the type to match the legal entity type code
                .build());


        testUserAccess = userEntityAccessRepository.save(UserEntityAccess.builder()
                .user(testUser)
                .legalEntity(testLegalEntity)
                .active(true)
                .grantedAt(Instant.now())
                .grantedBy("admin")
                .build());        testAccountType = accountTypeRepository.save(AccountType.builder()
                .code("TEST_" + timestamp)
                .name("Test Account Type")
                .build());
        
        testParentAccount = accountRepository.save(Account.builder()
                .code("PARENT001")
                .name("Parent Account")
                .legalEntity(testLegalEntity)
                .accountType(testAccountType)
                .build());

        testAccount = accountRepository.save(Account.builder()
                .code("TEST001")
                .name("Test Account")
                .description("Test Description")
                .legalEntity(testLegalEntity)
                .accountType(testAccountType)
                .parentAccount(testParentAccount)
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("test-user")
                .updatedBy("test-user")
                .build());
    }
}
