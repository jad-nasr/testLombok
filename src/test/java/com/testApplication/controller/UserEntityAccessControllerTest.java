package com.testApplication.controller;

import com.testApplication.dto.LegalEntityDTO;
import com.testApplication.dto.UserEntityAccessDTO;
import com.testApplication.service.UserEntityAccessService;
import com.testApplication.service.LegalEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserEntityAccessController.class)
@WithMockUser
class UserEntityAccessControllerTest {
        @Autowired
        private MockMvc mockMvc;
        @MockitoBean
        private UserEntityAccessService userEntityAccessService;

        @MockitoBean
        private LegalEntityService legalEntityService;

        private UserEntityAccessDTO activeAccessDTO;
        private UserEntityAccessDTO inactiveAccessDTO;

        @BeforeEach
        void setUp() {
                activeAccessDTO = UserEntityAccessDTO.builder()
                                .id(1L)
                                .userId(1L)
                                .username("testUser")
                                .legalEntityId(1L)
                                .legalEntityName("Test Legal Entity")
                                .active(true)
                                .grantedAt(Instant.now())
                                .grantedBy("testUser")
                                .build();

                inactiveAccessDTO = UserEntityAccessDTO.builder()
                                .id(2L)
                                .userId(2L)
                                .username("inactiveUser")
                                .legalEntityId(1L)
                                .legalEntityName("Test Legal Entity")
                                .active(false)
                                .grantedAt(Instant.now().minusSeconds(3600))
                                .grantedBy("testUser")
                                .revokedAt(Instant.now())
                                .revokedBy("testUser")
                                .build();
        }

        @Test
        void grantAccess_WithValidData_ShouldReturnCreated() throws Exception {
                when(userEntityAccessService.grantAccess(anyLong(), anyLong()))
                                .thenReturn(activeAccessDTO);

                mockMvc.perform(post("/api/user-entity-access/legal-entity/1/user/1")
                                .with(csrf()))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.userId").value(1))
                                .andExpect(jsonPath("$.legalEntityId").value(1))
                                .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        void revokeAccess_WithValidData_ShouldReturnOk() throws Exception {
                UserEntityAccessDTO revokedDTO = activeAccessDTO.builder()
                                .active(false)
                                .revokedAt(Instant.now())
                                .revokedBy("testUser")
                                .build();

                when(userEntityAccessService.revokeAccess(anyLong(), anyLong()))
                                .thenReturn(revokedDTO);

                mockMvc.perform(delete("/api/user-entity-access/legal-entity/1/user/1")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.active").value(false))
                                .andExpect(jsonPath("$.revokedBy").value("testUser"));
        }

        @Test
        void getUsersByLegalEntity_WithoutInactive_ShouldReturnOnlyActive() throws Exception {
                LegalEntityDTO legalEntityDTO = new LegalEntityDTO();
                legalEntityDTO.setUsers(Arrays.asList(activeAccessDTO, inactiveAccessDTO));
                when(legalEntityService.getLegalEntityById(anyLong()))
                                .thenReturn(Optional.of(legalEntityDTO));

                mockMvc.perform(get("/api/user-entity-access/legal-entity/1/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].userId").value(1))
                                .andExpect(jsonPath("$[0].active").value(true))
                                .andExpect(jsonPath("$[1]").doesNotExist());
        }

        @Test
        void getUsersByLegalEntity_WithInactive_ShouldReturnAll() throws Exception {
                LegalEntityDTO legalEntityDTO = new LegalEntityDTO();
                legalEntityDTO.setUsers(Arrays.asList(activeAccessDTO, inactiveAccessDTO));
                when(legalEntityService.getLegalEntityById(anyLong()))
                                .thenReturn(Optional.of(legalEntityDTO));

                mockMvc.perform(get("/api/user-entity-access/legal-entity/1/users?includeInactive=true"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].userId").value(1))
                                .andExpect(jsonPath("$[0].active").value(true))
                                .andExpect(jsonPath("$[1].userId").value(2))
                                .andExpect(jsonPath("$[1].active").value(false));
        }

        @Test
        void getLegalEntitiesByUser_WithoutInactive_ShouldReturnOnlyActive() throws Exception {
                when(userEntityAccessService.getLegalEntityAccessesByUser(anyLong()))
                                .thenReturn(Arrays.asList(activeAccessDTO, inactiveAccessDTO));

                mockMvc.perform(get("/api/user-entity-access/user/1/legal-entities"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].legalEntityId").value(1))
                                .andExpect(jsonPath("$[0].active").value(true))
                                .andExpect(jsonPath("$[1]").doesNotExist());
        }

        @Test
        void getLegalEntitiesByUser_WithInactive_ShouldReturnAll() throws Exception {
                when(userEntityAccessService.getLegalEntityAccessesByUser(anyLong()))
                                .thenReturn(Arrays.asList(activeAccessDTO, inactiveAccessDTO));

                mockMvc.perform(get("/api/user-entity-access/user/1/legal-entities?includeInactive=true"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].legalEntityId").value(1))
                                .andExpect(jsonPath("$[0].active").value(true))
                                .andExpect(jsonPath("$[1].legalEntityId").value(1))
                                .andExpect(jsonPath("$[1].active").value(false));
        }

        @Test
        void getAllUsersByLegalEntity_ShouldReturnAllUsers() throws Exception {
                LegalEntityDTO legalEntityDTO = new LegalEntityDTO();
                legalEntityDTO.setUsers(Arrays.asList(activeAccessDTO, inactiveAccessDTO));
                when(legalEntityService.getLegalEntityById(anyLong()))
                                .thenReturn(Optional.of(legalEntityDTO));

                mockMvc.perform(get("/api/user-entity-access/legal-entity/1/users/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].userId").value(1))
                                .andExpect(jsonPath("$[0].active").value(true))
                                .andExpect(jsonPath("$[1].userId").value(2))
                                .andExpect(jsonPath("$[1].active").value(false));
        }
}
