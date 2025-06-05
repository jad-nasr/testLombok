package com.testApplication.controller;

import com.testApplication.service.CsvImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CsvImportController.class)
class CsvImportControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CsvImportService csvImportService;

    private MockMultipartFile createTestFile() {
        return new MockMultipartFile(
            "file",
            "test.csv",
            "text/csv",
            "test content".getBytes()
        );
    }

    @Test
    void importTransactionLines_NoAuthentication_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(multipart("/api/import/transaction-lines")
                .file(createTestFile())
                .param("legalEntityId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "non_existing_user1", roles = {}) // User without any roles
    void importTransactionLines_UnauthorizedUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(multipart("/api/import/transaction-lines")
                .file(createTestFile())
                .param("legalEntityId", "1"))
                .andExpect(status().isForbidden());
    }
}