package com.testApplication.service;

import com.testApplication.dto.UserEntityAccessDTO;
import com.testApplication.model.LegalEntity;
import com.testApplication.model.User;
import com.testApplication.model.UserEntityAccess;
import com.testApplication.repository.LegalEntityRepository;
import com.testApplication.repository.UserEntityAccessRepository;
import com.testApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserEntityAccessServiceTest {

    @Mock
    private UserEntityAccessRepository userEntityAccessRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LegalEntityRepository legalEntityRepository;

    private UserEntityAccessService userEntityAccessService;
    private User testUser;
    private LegalEntity testLegalEntity;
    private UserEntityAccess testAccess;

    @BeforeEach
    void setUp() {
        // Setup security context
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        userEntityAccessService = new UserEntityAccessService(
                userEntityAccessRepository,
                userRepository,
                legalEntityRepository
        );

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testLegalEntity = new LegalEntity();
        testLegalEntity.setId(1L);
        testLegalEntity.setName("Test Legal Entity");

        testAccess = UserEntityAccess.builder()
                .id(1L)
                .user(testUser)
                .legalEntity(testLegalEntity)
                .active(true)
                .grantedAt(Instant.now())
                .grantedBy("testUser")
                .build();
    }

    @Test
    void grantAccess_WithValidData_ShouldSucceed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));        when(userEntityAccessRepository.findByUser_IdAndLegalEntity_Id(1L, 1L))
                .thenReturn(Optional.empty());
        when(userEntityAccessRepository.save(any(UserEntityAccess.class)))
                .thenReturn(testAccess);

        UserEntityAccessDTO result = userEntityAccessService.grantAccess(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getLegalEntityId());
        assertTrue(result.isActive());
    }    @Test
    void grantAccess_WhenAccessExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(userEntityAccessRepository.findByUser_IdAndLegalEntity_Id(1L, 1L))
                .thenReturn(Optional.of(testAccess));

        assertThrows(RuntimeException.class, () ->
            userEntityAccessService.grantAccess(1L, 1L)
        );
    }    @Test
    void revokeAccess_WithValidData_ShouldSucceed() {
        when(userEntityAccessRepository.findByUser_IdAndLegalEntity_Id(1L, 1L))
                .thenReturn(Optional.of(testAccess));
        when(userEntityAccessRepository.save(any(UserEntityAccess.class)))
                .thenReturn(testAccess);

        UserEntityAccessDTO result = userEntityAccessService.revokeAccess(1L, 1L);

        assertNotNull(result);
        assertFalse(result.isActive());
        assertNotNull(result.getRevokedAt());
        assertEquals("testUser", result.getRevokedBy());
    }    @Test
    void getUserAccessesByLegalEntity_ShouldReturnList() {
        when(userEntityAccessRepository.findByLegalEntity_Id(anyLong()))
                .thenReturn(Arrays.asList(testAccess));

        List<UserEntityAccessDTO> result = userEntityAccessService.getUserAccessesByLegalEntity(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }    @Test
    void getLegalEntityAccessesByUser_ShouldReturnList() {
        when(userEntityAccessRepository.findByUser_Id(anyLong()))
                .thenReturn(Arrays.asList(testAccess));

        List<UserEntityAccessDTO> result = userEntityAccessService.getLegalEntityAccessesByUser(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getLegalEntityId());
    }

    @Test
    void getAllUserAccessesByLegalEntity_ShouldReturnAllAccesses() {
        // Create legal entity with user accesses
        Set<UserEntityAccess> accesses = new HashSet<>(Arrays.asList(testAccess));
        testLegalEntity.setUserAccesses(accesses);

        when(legalEntityRepository.findById(1L))
                .thenReturn(Optional.of(testLegalEntity));

        List<UserEntityAccessDTO> result = userEntityAccessService.getAllUserAccessesByLegalEntity(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getLegalEntityId());
        assertEquals(1L, result.get(0).getUserId());
        assertTrue(result.get(0).isActive());
        verify(legalEntityRepository).findById(1L);
    }
}
