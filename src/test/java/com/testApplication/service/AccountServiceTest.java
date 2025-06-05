package com.testApplication.service;

import com.testApplication.model.Account;
import com.testApplication.model.AccountType;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.AccountRepository;
import com.testApplication.repository.AccountTypeRepository;
import com.testApplication.repository.LegalEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {    
    
    @Autowired
    private AccountService accountService;    
    
    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private AccountTypeRepository accountTypeRepository;

    @MockitoBean
    private LegalEntityRepository legalEntityRepository;

    private Account testAccount;
    private AccountType testAccountType;
    private LegalEntity testLegalEntity;
    private Account testParentAccount;

    @BeforeEach
    void setUp() {
        // Set up test entities
        testLegalEntity = new LegalEntity();
        testLegalEntity.setId(1L);
        testLegalEntity.setName("Test Legal Entity");

        testAccountType = new AccountType();
        testAccountType.setId(1L);
        testAccountType.setCode("TEST");
        testAccountType.setName("Test Account Type");

        testParentAccount = new Account();
        testParentAccount.setId(1L);
        testParentAccount.setCode("PARENT");
        testParentAccount.setName("Parent Account");
        testParentAccount.setAccountType(testAccountType);
        testParentAccount.setLegalEntity(testLegalEntity);

        testAccount = new Account();
        testAccount.setId(2L);
        testAccount.setCode("TEST001");
        testAccount.setName("Test Account");
        testAccount.setDescription("Test Description");
        testAccount.setAccountType(testAccountType);
        testAccount.setLegalEntity(testLegalEntity);
        testAccount.setParentAccount(testParentAccount);
        testAccount.setActive(true);
        testAccount.setCreatedAt(Instant.now());
        testAccount.setUpdatedAt(Instant.now());
        testAccount.setCreatedBy("test-user");
        testAccount.setUpdatedBy("test-user");
    }

    @Test
    void createAccount_WithValidData_ShouldSucceed() {
        // Arrange
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountTypeRepository.findById(1L)).thenReturn(Optional.of(testAccountType));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testParentAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account result = accountService.createAccount(1L, 1L, 1L, testAccount);

        // Assert
        assertNotNull(result);
        assertEquals("TEST001", result.getCode());
        assertEquals("Test Account", result.getName());
        assertEquals(testAccountType, result.getAccountType());
        assertEquals(testLegalEntity, result.getLegalEntity());
        assertEquals(testParentAccount, result.getParentAccount());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_WithInvalidLegalEntity_ShouldThrowException() {
        // Arrange
        when(legalEntityRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            accountService.createAccount(99L, 1L, null, testAccount)
        );
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_WithInvalidAccountType_ShouldThrowException() {
        // Arrange
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountTypeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            accountService.createAccount(1L, 99L, null, testAccount)
        );
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccountById_WhenExists_ShouldReturnAccount() {
        // Arrange
        when(accountRepository.findById(2L)).thenReturn(Optional.of(testAccount));

        // Act
        Optional<Account> result = accountService.getAccountById(2L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TEST001", result.get().getCode());
    }

    @Test
    void getAccountById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Account> result = accountService.getAccountById(99L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllAccounts_ShouldReturnList() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount, testParentAccount);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertEquals(2, result.size());
        verify(accountRepository).findAll();
    }

    @Test
    void getAccountsByLegalEntity_WhenLegalEntityExists_ShouldReturnList() {
        // Arrange
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountRepository.findByLegalEntity(testLegalEntity))
            .thenReturn(Arrays.asList(testAccount, testParentAccount));

        // Act
        List<Account> result = accountService.getAccountsByLegalEntity(1L);

        // Assert
        assertEquals(2, result.size());
        verify(accountRepository).findByLegalEntity(testLegalEntity);
    }

    @Test
    void getAccountsByLegalEntity_WhenLegalEntityNotExists_ShouldThrowException() {
        // Arrange
        when(legalEntityRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            accountService.getAccountsByLegalEntity(99L)
        );
    }

    @Test
    void updateAccount_WithValidData_ShouldSucceed() {
        // Arrange
        when(accountRepository.findById(2L)).thenReturn(Optional.of(testAccount));
        when(accountTypeRepository.findById(1L)).thenReturn(Optional.of(testAccountType));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testParentAccount));
        
        Account updatedAccount = new Account();
        updatedAccount.setCode("UPDATED001");
        updatedAccount.setName("Updated Account");
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act
        Account result = accountService.updateAccount(2L, updatedAccount, 1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("UPDATED001", result.getCode());
        assertEquals("Updated Account", result.getName());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void updateAccount_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            accountService.updateAccount(99L, testAccount, null, null)
        );
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void deleteAccount_ShouldCallRepository() {
        // Arrange
        doNothing().when(accountRepository).deleteById(2L);

        // Act
        accountService.deleteAccount(2L);

        // Verify
        verify(accountRepository).deleteById(2L);
    }
}
