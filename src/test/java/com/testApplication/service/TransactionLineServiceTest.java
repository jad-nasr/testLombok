package com.testApplication.service;

import com.testApplication.model.TransactionLine;
import com.testApplication.model.Transaction;
import com.testApplication.model.Account;
import com.testApplication.repository.TransactionLineRepository;
import com.testApplication.repository.TransactionRepository;
import com.testApplication.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class TransactionLineServiceTest {

    @Autowired
    private TransactionLineService transactionLineService;

    @MockitoBean
    private TransactionLineRepository transactionLineRepository;

    @MockitoBean
    private TransactionRepository transactionRepository;

    @MockitoBean
    private AccountRepository accountRepository;

    private TransactionLine testTransactionLine;
    private Transaction testTransaction;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
            .id(1L)
            .code("ACC001")
            .name("Test Account")
            .build();

        testTransaction = Transaction.builder()
            .id(1L)
            .transactionCode("TRX001")
            .transactionType("PAYMENT")
            .date(Instant.now())
            .build();

        testTransactionLine = TransactionLine.builder()
            .id(1L)
            .transaction(testTransaction)
            .account(testAccount)
            .amount(BigDecimal.valueOf(100.00))
            .description("Test Transaction Line")
            .isDebit(true)
            .build();
    }

    @Test
    void createTransactionLine_WithValidData_ShouldSucceed() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(transactionLineRepository.save(any(TransactionLine.class))).thenReturn(testTransactionLine);

        // Act
        TransactionLine result = transactionLineService.createTransactionLine(testTransactionLine, 1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100.00), result.getAmount());
        assertEquals("Test Transaction Line", result.getDescription());
        assertTrue(result.isDebit());
        assertEquals(testTransaction, result.getTransaction());
        assertEquals(testAccount, result.getAccount());
        verify(transactionLineRepository).save(any(TransactionLine.class));
    }

    @Test
    void createTransactionLine_WithInvalidTransaction_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionLineService.createTransactionLine(testTransactionLine, 99L, 1L)
        );
        verify(transactionLineRepository, never()).save(any(TransactionLine.class));
    }

    @Test
    void createTransactionLine_WithInvalidAccount_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionLineService.createTransactionLine(testTransactionLine, 1L, 99L)
        );
        verify(transactionLineRepository, never()).save(any(TransactionLine.class));
    }

    @Test
    void getTransactionLineById_WhenExists_ShouldReturnTransactionLine() {
        // Arrange
        when(transactionLineRepository.findById(1L)).thenReturn(Optional.of(testTransactionLine));

        // Act
        Optional<TransactionLine> result = transactionLineService.getTransactionLineById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(100.00), result.get().getAmount());
        assertEquals("Test Transaction Line", result.get().getDescription());
    }

    @Test
    void getTransactionLineById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(transactionLineRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<TransactionLine> result = transactionLineService.getTransactionLineById(99L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllTransactionLines_ShouldReturnList() {
        // Arrange
        when(transactionLineRepository.findAll()).thenReturn(Arrays.asList(testTransactionLine));

        // Act
        List<TransactionLine> result = transactionLineService.getAllTransactionLines();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(transactionLineRepository).findAll();
    }

    @Test
    void getTransactionLinesByTransaction_WhenTransactionExists_ShouldReturnList() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionLineRepository.findByTransaction(testTransaction))
            .thenReturn(Arrays.asList(testTransactionLine));

        // Act
        List<TransactionLine> result = transactionLineService.getTransactionLinesByTransaction(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(transactionLineRepository).findByTransaction(testTransaction);
    }

    @Test
    void getTransactionLinesByTransaction_WhenTransactionNotExists_ShouldThrowException() {
        // Arrange
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionLineService.getTransactionLinesByTransaction(99L)
        );
    }

    @Test
    void getTransactionLinesByAccount_WhenAccountExists_ShouldReturnList() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(transactionLineRepository.findByAccount(testAccount))
            .thenReturn(Arrays.asList(testTransactionLine));

        // Act
        List<TransactionLine> result = transactionLineService.getTransactionLinesByAccount(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(transactionLineRepository).findByAccount(testAccount);
    }

    @Test
    void getTransactionLinesByAccount_WhenAccountNotExists_ShouldThrowException() {
        // Arrange
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionLineService.getTransactionLinesByAccount(99L)
        );
    }

    @Test
    void updateTransactionLine_WithValidData_ShouldSucceed() {
        // Arrange
        when(transactionLineRepository.findById(1L)).thenReturn(Optional.of(testTransactionLine));
        
        TransactionLine updatedLine = TransactionLine.builder()
            .amount(BigDecimal.valueOf(200.00))
            .description("Updated Description")
            .isDebit(false)
            .build();
        
        when(transactionLineRepository.save(any(TransactionLine.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionLine result = transactionLineService.updateTransactionLine(1L, updatedLine);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(200.00), result.getAmount());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.isDebit());
        verify(transactionLineRepository).save(any(TransactionLine.class));
    }

    @Test
    void updateTransactionLine_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(transactionLineRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            transactionLineService.updateTransactionLine(99L, testTransactionLine)
        );
        verify(transactionLineRepository, never()).save(any(TransactionLine.class));
    }

    @Test
    void deleteTransactionLine_ShouldCallRepository() {
        // Arrange
        doNothing().when(transactionLineRepository).deleteById(1L);

        // Act
        transactionLineService.deleteTransactionLine(1L);

        // Verify
        verify(transactionLineRepository).deleteById(1L);
    }
}
