package com.testApplication.service;

import com.testApplication.dto.TransactionDTO;
import com.testApplication.model.Transaction;
import com.testApplication.model.Customer;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.TransactionRepository;
import com.testApplication.repository.CustomerRepository;
import com.testApplication.repository.LegalEntityRepository;
import com.testApplication.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @MockitoBean
    private TransactionRepository transactionRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private LegalEntityRepository legalEntityRepository;

    @MockitoBean
    private TransactionMapper transactionMapper;

    private Transaction testTransaction;
    private TransactionDTO testTransactionDTO;
    private Customer testCustomer;
    private LegalEntity testLegalEntity;

    @BeforeEach
    void setUp() {
        // Set up security context with test user
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test-user");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Set up test entities
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");

        testLegalEntity = new LegalEntity();
        testLegalEntity.setId(1L);
        testLegalEntity.setName("Test Legal Entity");

        testTransaction = Transaction.builder()
                .id(1L)
                .transactionCode("TRX001")
                .transactionType("PAYMENT")
                .date(Instant.now())
                .description("Test Transaction")
                .approvalStatus("PENDING")
                .amount(BigDecimal.valueOf(100.00))
                .currency("USD")
                .customer(testCustomer)
                .legalEntity(testLegalEntity)
                .build();

        testTransactionDTO = TransactionDTO.builder()
                .id(1L)
                .transactionCode("TRX001")
                .transactionType("PAYMENT")
                .date(testTransaction.getDate())
                .description("Test Transaction")
                .approvalStatus("PENDING")
                .amount(BigDecimal.valueOf(100.00))
                .currency("USD")
                .legalEntityId(1L)
                .build();
    }

    @Test
    void getAllTransactions_ShouldReturnList() {
        // Arrange
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(testTransaction));
        when(transactionMapper.toDTOList(any())).thenReturn(Arrays.asList(testTransactionDTO));

        // Act
        List<TransactionDTO> result = transactionService.getAllTransactions();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(transactionRepository).findAll();
        verify(transactionMapper).toDTOList(any());
    }

    @Test
    void getTransactionById_WhenExists_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionMapper.toDTO(testTransaction)).thenReturn(testTransactionDTO);

        // Act
        Optional<TransactionDTO> result = transactionService.getTransactionById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TRX001", result.get().getTransactionCode());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void getTransactionById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<TransactionDTO> result = transactionService.getTransactionById(99L);

        // Assert
        assertTrue(result.isEmpty());
        verify(transactionRepository).findById(99L);
    }

    @Test
    void createTransaction_WithValidData_ShouldSucceed() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(transactionRepository.findByTransactionCodeAndLegalEntityId(anyString(), anyLong()))
            .thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // Act
        TransactionDTO result = transactionService.createTransaction(testTransactionDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("TRX001", result.getTransactionCode());
        assertEquals("PAYMENT", result.getTransactionType());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WithExistingCode_ShouldThrowException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(transactionRepository.findByTransactionCodeAndLegalEntityId(anyString(), anyLong()))
            .thenReturn(Optional.of(testTransaction));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionService.createTransaction(testTransactionDTO, 1L)
        );
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_WithInvalidCustomer_ShouldThrowException() {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionService.createTransaction(testTransactionDTO, 99L)
        );
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_WithValidData_ShouldSucceed() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionMapper.toDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        testTransactionDTO.setDescription("Updated Description");

        // Act
        TransactionDTO result = transactionService.updateTransaction(1L, testTransactionDTO);

        // Assert
        assertNotNull(result);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionService.updateTransaction(99L, testTransactionDTO)
        );
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_WhenExists_ShouldSucceed() {
        // Arrange
        when(transactionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(transactionRepository).deleteById(1L);

        // Act
        transactionService.deleteTransaction(1L);

        // Verify
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void deleteTransaction_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(transactionRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionService.deleteTransaction(99L)
        );
        verify(transactionRepository, never()).deleteById(anyLong());
    }

    @Test
    void getTransactionsByCustomer_WhenCustomerExists_ShouldReturnList() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionRepository.findByCustomer(testCustomer))
            .thenReturn(Arrays.asList(testTransaction));
        when(transactionMapper.toDTOList(any())).thenReturn(Arrays.asList(testTransactionDTO));

        // Act
        List<TransactionDTO> result = transactionService.getTransactionsByCustomer(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(transactionRepository).findByCustomer(testCustomer);
    }

    @Test
    void getTransactionsByCustomer_WhenCustomerNotExists_ShouldThrowException() {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionService.getTransactionsByCustomer(99L)
        );
    }

    @Test
    void findByTransactionCodeAndLegalEntity_WhenExists_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findByTransactionCodeAndLegalEntityId("TRX001", 1L))
            .thenReturn(Optional.of(testTransaction));
        when(transactionMapper.toDTO(testTransaction)).thenReturn(testTransactionDTO);

        // Act
        Optional<TransactionDTO> result = transactionService.findByTransactionCodeAndLegalEntity("TRX001", 1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TRX001", result.get().getTransactionCode());
    }
}
