package com.testApplication.service;

import com.testApplication.dto.AccountDTO;
import com.testApplication.model.*;
import com.testApplication.repository.*;
import com.testApplication.mapper.AccountMapper;
import com.testApplication.util.SetupTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AccountServiceTest extends SetupTestData {    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;
    
    @Autowired
    private LegalEntityRepository legalEntityRepository;

    @Autowired
    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        // Run base class setup first
        setUpBase();
    }    @Test    void createAccount_WhenUserHasAccess_ShouldSucceed() {
        // No need to mock security service - we have real user access records
        
        // Create test account DTO
        String uniqueCode = "TEST" + System.currentTimeMillis();
        AccountDTO newAccountDTO = AccountDTO.builder()
                .code(uniqueCode)
                .name("Test Account 2")
                .description("Test Description 2")
                .build();
                
        // Act
        AccountDTO result = accountService.createAccount(
            testLegalEntity.getId(), 
            testAccountType.getId(), 
            testParentAccount.getId(), 
            newAccountDTO
        );

        // Assert
        assertNotNull(result);
        assertEquals(uniqueCode, result.getCode());
        assertEquals("Test Account 2", result.getName());
        assertEquals("Test Description 2", result.getDescription());
        
        // Verify the account was actually created in the database
        Optional<Account> createdAccount = accountRepository.findByCodeAndLegalEntity_Id(uniqueCode, testLegalEntity.getId());
        assertTrue(createdAccount.isPresent());
        assertEquals(testLegalEntity.getId(), createdAccount.get().getLegalEntity().getId());
        assertEquals(testAccountType.getId(), createdAccount.get().getAccountType().getId());
        assertEquals(testParentAccount.getId(), createdAccount.get().getParentAccount().getId());
    }

    @Test    void createAccount_WhenUserDoesNotHaveAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        // Switch to unauthorized user context
        testUserDetails = org.springframework.security.core.userdetails.User.builder()
            .username(testUnauthorizedUser.getUsername())
            .password(testUnauthorizedUser.getPassword())
            .roles("USER")
            .build();
        
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                testUserDetails,
                null,
                testUserDetails.getAuthorities()
            )
        );        AccountDTO newAccountDTO = AccountDTO.builder()
                .code("TEST001")
                .name("Test Account")
                .build();        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> accountService.createAccount(1L, 1L, null, newAccountDTO));
    }    @Test    void getAccountById_WhenUserHasAccess_ShouldReturnAccount() {
        // Arrange
        // Set up authorized user context
        testUserDetails = org.springframework.security.core.userdetails.User.builder()
            .username(testUser.getUsername())
            .password(testUser.getPassword())
            .roles("USER", "ADMIN")
            .build();
        
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                testUserDetails,
                null,
                testUserDetails.getAuthorities()
            )
        );
        
        // Act
        Optional<AccountDTO> result = accountService.getAccountById(testLegalEntity.getId(), testAccount.getId());

        // Assert
        assertTrue(result.isPresent());        assertEquals("TEST001", result.get().getCode());
    }

    @Test    void getAccountById_WhenUserDoesNotHaveAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        // Switch to unauthorized user context
        testUserDetails = org.springframework.security.core.userdetails.User.builder()
            .username(testUnauthorizedUser.getUsername())
            .password(testUnauthorizedUser.getPassword())
            .roles("USER")
            .build();
          SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                testUserDetails,
                null,
                testUserDetails.getAuthorities()
            )
        );

        assertThrows(AccessDeniedException.class, () -> accountService.getAccountById(1L, 2L));
    }    @Test    void getAccountsByLegalEntity_WhenUserHasAccess_ShouldReturnList() {
        // Arrange
        // Set up authorized user context
        testUserDetails = org.springframework.security.core.userdetails.User.builder()
            .username(testUser.getUsername())
            .password(testUser.getPassword())
            .roles("USER", "ADMIN")
            .build();
        
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                testUserDetails,
                null,
                testUserDetails.getAuthorities()
            )
        );
        
        // Act
        List<AccountDTO> result = accountService.getAccountsByLegalEntity(testLegalEntity.getId());        // Assert
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        // One of the accounts should match our test account
        assertTrue(result.stream().anyMatch(account -> "TEST001".equals(account.getCode())));
        // No verification needed - using real security service
    }

    @Test    void getAccountsByLegalEntity_WhenUserDoesNotHaveAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        // Switch to unauthorized user context
        testUserDetails = org.springframework.security.core.userdetails.User.builder()
            .username(testUnauthorizedUser.getUsername())
            .password(testUnauthorizedUser.getPassword())
            .roles("USER")
            .build();
        
        SecurityContextHolder.getContext().setAuthentication(
            new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                testUserDetails,
                null,
                testUserDetails.getAuthorities()
            )        );               // Act & Assert
        assertThrows(AccessDeniedException.class, () -> accountService.getAccountsByLegalEntity(1L));
    }
}
