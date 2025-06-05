package com.testApplication.controller;

import com.testApplication.dto.AccountAllocationTemplateDTO;
import com.testApplication.dto.AccountAllocationTemplateDTO.AccountAllocationDetails;
import com.testApplication.service.AccountAllocationTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountAllocationTemplateController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountAllocationTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;    @MockitoBean
    private AccountAllocationTemplateService templateService;

    private AccountAllocationTemplateDTO testTemplateDTO;
    private Set<AccountAllocationDetails> testAccountDetails;

    @BeforeEach
    void setUp() {
        testAccountDetails = new HashSet<>();
        testAccountDetails.add(AccountAllocationDetails.builder()
                .accountId(1L)
                .accountCode("TEST001")
                .allocationOrder(1)
                .isSource(true)
                .build());

        testTemplateDTO = AccountAllocationTemplateDTO.builder()
                .id(1L)                .code("TEMPLATE001")
                .name("Test Template")
                .description("Test Description")
                .allocation_details(testAccountDetails)
                .build();
    }

    @Test
    void getAllTemplates_ShouldReturnList() throws Exception {
        when(templateService.getAllTemplates()).thenReturn(List.of(testTemplateDTO));

        mockMvc.perform(get("/api/allocation-templates"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("TEMPLATE001"))
                .andExpect(jsonPath("$[0].allocation_details[0].accountId").value(1))
                .andExpect(jsonPath("$[0].allocation_details[0].accountCode").value("TEST001"))
                .andExpect(jsonPath("$[0].allocation_details[0].allocationOrder").value(1))
                .andExpect(jsonPath("$[0].allocation_details[0].isSource").value(true));
    }

    @Test
    void getTemplateById_WhenExists_ShouldReturnTemplate() throws Exception {
        when(templateService.getTemplateById(1L)).thenReturn(Optional.of(testTemplateDTO));

        mockMvc.perform(get("/api/allocation-templates/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("TEMPLATE001"))
                .andExpect(jsonPath("$.allocation_details[0].accountId").value(1))
                .andExpect(jsonPath("$.allocation_details[0].isSource").value(true));
    }

    @Test
    void getTemplateById_WhenNotExists_ShouldReturn404() throws Exception {
        when(templateService.getTemplateById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/allocation-templates/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTemplateByCode_WhenExists_ShouldReturnTemplate() throws Exception {
        when(templateService.getTemplateByCode("TEMPLATE001")).thenReturn(Optional.of(testTemplateDTO));

        mockMvc.perform(get("/api/allocation-templates/by-code/TEMPLATE001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("TEMPLATE001"))
                .andExpect(jsonPath("$.allocation_details[0].accountId").value(1));
    }

    @Test
    void getTemplatesByType_ShouldReturnList() throws Exception {
        when(templateService.getTemplatesByType(true)).thenReturn(List.of(testTemplateDTO));

        mockMvc.perform(get("/api/allocation-templates/by-type")
                .param("isSource", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].allocation_details[0].isSource").value(true));
    }

    @Test
    void createTemplate_WithValidData_ShouldCreateTemplate() throws Exception {
        when(templateService.createTemplate(any(AccountAllocationTemplateDTO.class)))
                .thenReturn(testTemplateDTO);

        mockMvc.perform(post("/api/allocation-templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTemplateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("TEMPLATE001"))
                .andExpect(jsonPath("$.allocation_details[0].accountId").value(1))
                .andExpect(jsonPath("$.allocation_details[0].isSource").value(true));
    }

    @Test
    void updateTemplate_WithValidData_ShouldUpdateTemplate() throws Exception {
        when(templateService.updateTemplate(eq(1L), any(AccountAllocationTemplateDTO.class)))
                .thenReturn(testTemplateDTO);

        mockMvc.perform(put("/api/allocation-templates/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTemplateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("TEMPLATE001"))
                .andExpect(jsonPath("$.allocation_details[0].accountId").value(1))
                .andExpect(jsonPath("$.allocation_details[0].isSource").value(true));
    }

    @Test
    void deleteTemplate_WhenExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/allocation-templates/1"))
                .andExpect(status().isNoContent());
    }
}
