package com.testApplication.controller;

import com.testApplication.dto.TransactionLineDTO;
import com.testApplication.service.CsvImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CsvImportController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
class CsvImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CsvImportService csvImportService;

    private TransactionLineDTO testTransactionLineDTO;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
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

        // Read actual test CSV file from resources
        Resource resource = new ClassPathResource("test_transactions.csv");
        byte[] csvBytes;
        try {
            csvBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read test CSV file", e);
        }

        testFile = new MockMultipartFile(
                "file",
                "test_transactions.csv",
                "text/csv",
                csvBytes);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importTransactionLines_ValidFile_ShouldSucceed() throws Exception {
        when(csvImportService.importTransactionLinesFromCsv(any(), anyLong()))
                .thenReturn(Arrays.asList(testTransactionLineDTO));

        mockMvc.perform(multipart("/api/import/transaction-lines")
                .file(testFile)
                .param("legalEntityId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionCode").value("TRANS004"))
                .andExpect(jsonPath("$[0].accountCode").value("ACC001"))
                .andExpect(jsonPath("$[0].amount").value(1000));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importTransactionLines_InvalidFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "invalid.csv",
                "text/csv",
                "invalid,csv,format".getBytes());

        when(csvImportService.importTransactionLinesFromCsv(any(), anyLong()))
                .thenThrow(new RuntimeException("Invalid CSV format"));

        mockMvc.perform(multipart("/api/import/transaction-lines")
                .file(invalidFile)
                .param("legalEntityId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importTransactionLines_NoFile_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(multipart("/api/import/transaction-lines")
                .param("legalEntityId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importTransactionLines_MissingLegalEntityId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(multipart("/api/import/transaction-lines")
                .file(testFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importTransactionLines_EmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                "".getBytes());

        mockMvc.perform(multipart("/api/import/transaction-lines")
                .file(emptyFile)
                .param("legalEntityId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importTransactionLines_WrongContentType_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile wrongTypeFile = new MockMultipartFile(
                "file",
                "data.txt",
                "text/plain", // Wrong content type
                testFile.getBytes());

        mockMvc.perform(multipart("/api/import/transaction-lines")
                .file(wrongTypeFile)
                .param("legalEntityId", "1"))
                .andExpect(status().isBadRequest());
    }
}
