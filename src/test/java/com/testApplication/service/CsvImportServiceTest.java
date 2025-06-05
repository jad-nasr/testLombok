package com.testApplication.service;

import com.testApplication.model.*;
import com.testApplication.repository.*;
import com.testApplication.dto.TransactionLineDTO;
import com.testApplication.mapper.TransactionLineMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CsvImportServiceTest {

    @MockitoBean
    private TransactionLineMapper transactionLineMapper;

    @MockitoBean
    private TransactionLineRepository transactionLineRepository;

    @MockitoBean
    private TransactionRepository transactionRepository;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private LegalEntityRepository legalEntityRepository;

    @MockitoBean
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private CsvImportService csvImportService;

    private LegalEntity testLegalEntity;
    private Account testAccount;
    private Transaction testTransaction;
    private TransactionLine testTransactionLine;
    private TransactionLineDTO testTransactionLineDTO;
    private MockMultipartFile testFile;
    private AccountType testAccountType;

    @BeforeEach
    void setUp() {
        testLegalEntity = LegalEntity.builder()
            .id(1L)
            .name("Test Entity")
            .build();

        testAccountType = AccountType.builder()
            .id(1L)
            .code("ASSET")
            .name("Asset Account")
            .build();

        testAccount = Account.builder()
            .id(1L)
            .code("ACC001")
            .name("Test Account")
            .legalEntity(testLegalEntity)
            .accountType(testAccountType)
            .build();

        testTransaction = Transaction.builder()
            .id(1L)
            .transactionCode("TRANS004")
            .transactionType("CSV_IMPORT")
            .date(Instant.now())
            .approvalStatus("PENDING")
            .legalEntity(testLegalEntity)
            .build();

        testTransactionLine = TransactionLine.builder()
            .id(1L)
            .transaction(testTransaction)
            .account(testAccount)
            .amount(BigDecimal.valueOf(1000))
            .description("Initial deposit")
            .isDebit(false)
            .build();

        testTransactionLineDTO = TransactionLineDTO.builder()
            .id(1L)
            .transactionCode("TRANS004")
            .accountCode("ACC001")
            .accountName("Test Account")
            .amount(BigDecimal.valueOf(1000))
            .description("Initial deposit")
            .isDebit(false)
            .transactionStatus("PENDING")
            .build();

        String csvContent = 
            "transaction_code,account_code,amount,description,is_debit\n" +
            "TRANS004,ACC001,1000,Initial deposit,false\n" +
            "TRANS004,ACC002,1000,Initial deposit transfer,true\n" +
            "TRANS005,ACC001,500,Payment received,false\n" +
            "TRANS005,ACC003,500,Payment distribution,true";
        
        testFile = new MockMultipartFile(
            "file",
            "test_transactions.csv",
            "text/csv",
            csvContent.getBytes()
        );
    }    @Test
    void importTransactionLines_ValidFile_ShouldSucceed() throws IOException {
        // Arrange
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountRepository.findByCodeAndLegalEntityId(anyString(), anyLong()))
            .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByTransactionCodeAndLegalEntityId(anyString(), anyLong()))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(testTransaction));
        when(transactionLineRepository.saveAll(any())).thenReturn(Arrays.asList(testTransactionLine));
        when(transactionLineMapper.toDTOList(any())).thenReturn(Arrays.asList(testTransactionLineDTO));

        // Act
        List<TransactionLineDTO> result = csvImportService.importTransactionLinesFromCsv(testFile, 1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        TransactionLineDTO dto = result.get(0);
        assertEquals("TRANS004", dto.getTransactionCode());
        assertEquals("ACC001", dto.getAccountCode());
        assertEquals(BigDecimal.valueOf(1000), dto.getAmount());
        
        // Verify repository calls
        verify(transactionRepository, atLeastOnce()).save(any());
        verify(transactionLineRepository).saveAll(any());
        verify(transactionLineMapper).toDTOList(any());
    }

    @Test
    void importTransactionLinesFromCsv_InvalidFile_ShouldThrowException() {
        // Arrange
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file",
            "invalid.csv",
            "text/csv",
            "invalid,csv,format".getBytes()
        );

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            csvImportService.importTransactionLinesFromCsv(invalidFile, 1L)
        );
    }

    @Test
    void importTransactionLinesFromCsv_LegalEntityNotFound_ShouldThrowException() {
        // Arrange
        when(legalEntityRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            csvImportService.importTransactionLinesFromCsv(testFile, 99L),
            "Legal Entity not found: 99"
        );
    }    @Test
    void importTransactionLinesFromCsv_AccountNotFound_ShouldCreateNewAccount() {
        // Arrange
        String singleLineCsvContent = 
            "transaction_code,account_code,amount,description,is_debit\n" +
            "TRANS004,NEW_ACC001,1000,Initial deposit,false";
            
        MockMultipartFile singleLineFile = new MockMultipartFile(
            "file",
            "test_transactions.csv",
            "text/csv",
            singleLineCsvContent.getBytes()
        );

        Account newAccount = Account.builder()
            .id(1L)
            .code("NEW_ACC001")
            .name("Imported: NEW_ACC001")
            .legalEntity(testLegalEntity)
            .accountType(testAccountType)
            .active(true)
            .build();

        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountRepository.findByCodeAndLegalEntityId(eq("NEW_ACC001"), anyLong()))
            .thenReturn(Optional.empty());
        when(accountTypeRepository.findByCode("ASSET")).thenReturn(Optional.of(testAccountType));
        when(accountRepository.save(any())).thenReturn(newAccount);
        when(transactionRepository.findByTransactionCodeAndLegalEntityId(anyString(), anyLong()))
            .thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenReturn(testTransaction);
        when(transactionLineRepository.saveAll(any())).thenReturn(Arrays.asList(testTransactionLine));
        when(transactionLineMapper.toDTOList(any())).thenReturn(Arrays.asList(testTransactionLineDTO));

        // Act
        List<TransactionLineDTO> result = csvImportService.importTransactionLinesFromCsv(singleLineFile, 1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Verify account creation behavior
        verify(accountRepository, times(1)).findByCodeAndLegalEntityId(eq("NEW_ACC001"), anyLong());
        verify(accountTypeRepository, times(1)).findByCode("ASSET");
        verify(accountRepository, times(1)).save(argThat(account -> 
            account.getCode().equals("NEW_ACC001") &&
            account.getName().equals("Imported: NEW_ACC001") &&
            account.getAccountType() == testAccountType &&
            account.getLegalEntity() == testLegalEntity &&
            account.isActive()
        ));
    }

    @Test
    void importTransactionLinesFromCsv_AccountTypeNotFound_ShouldThrowException() {
        // Arrange
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountRepository.findByCodeAndLegalEntityId(anyString(), anyLong()))
            .thenReturn(Optional.empty());
        when(accountTypeRepository.findByCode("ASSET")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            csvImportService.importTransactionLinesFromCsv(testFile, 1L)
        );
        assertTrue(exception.getMessage().contains("Default ASSET account type not found"));
    }

    @Test
    void importTransactionLines_ExistingTransaction_ShouldReuseTransaction() {
        // Arrange
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountRepository.findByCodeAndLegalEntityId(anyString(), anyLong()))
            .thenReturn(Optional.of(testAccount));
        when(transactionRepository.findByTransactionCodeAndLegalEntityId(anyString(), anyLong()))
            .thenReturn(Optional.of(testTransaction));
        when(transactionLineRepository.saveAll(any())).thenReturn(Arrays.asList(testTransactionLine));
        when(transactionLineMapper.toDTOList(any())).thenReturn(Arrays.asList(testTransactionLineDTO));

        // Act
        List<TransactionLineDTO> result = csvImportService.importTransactionLinesFromCsv(testFile, 1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(transactionRepository, never()).save(any());
        verify(transactionLineRepository).saveAll(any());
    }
}