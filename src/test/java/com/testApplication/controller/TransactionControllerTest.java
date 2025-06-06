package com.testApplication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testApplication.dto.TransactionDTO;
import com.testApplication.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import com.testApplication.config.SecurityConfig;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)  // Disable security filters for testing
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    private TransactionDTO testTransactionDTO;

    @BeforeEach
    void setUp() {
        testTransactionDTO = TransactionDTO.builder()
                .id(1L)
                .transactionCode("TRX001")
                .transactionType("PAYMENT")
                .date(Instant.now())
                .description("Test Transaction")
                .approvalStatus("PENDING")
                .amount(BigDecimal.valueOf(100.00))
                .currency("USD")
                .customerId(1L)  // Remove if not in DTO
                .legalEntityId(1L)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")  // Add mock user with ADMIN role
    void getAllTransactions_ShouldReturnList() throws Exception {
        when(transactionService.getAllTransactions())
                .thenReturn(Arrays.asList(testTransactionDTO));

        mockMvc.perform(get("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionCode").value("TRX001"))
                .andExpect(jsonPath("$[0].amount").value(100.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTransactionById_WhenExists_ShouldReturnTransaction() throws Exception {
        when(transactionService.getTransactionById(1L))
                .thenReturn(Optional.of(testTransactionDTO));

        mockMvc.perform(get("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCode").value("TRX001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTransactionById_WhenNotExists_ShouldReturn404() throws Exception {
        when(transactionService.getTransactionById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transactions/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }    @Test
    @WithMockUser(roles = "ADMIN")
    void createTransaction_WithValidData_ShouldReturnCreated() throws Exception {
        when(transactionService.createTransaction(any(TransactionDTO.class), anyLong()))
                .thenReturn(testTransactionDTO);

        mockMvc.perform(post("/api/transactions/legal-entity/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("customerId", "1")
                .content(objectMapper.writeValueAsString(testTransactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionCode").value("TRX001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTransaction_WithValidData_ShouldReturnUpdated() throws Exception {
        when(transactionService.updateTransaction(anyLong(), any(TransactionDTO.class)))
                .thenReturn(testTransactionDTO);

        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCode").value("TRX001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTransaction_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}