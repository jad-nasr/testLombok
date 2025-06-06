package com.testApplication.service;

import com.testApplication.dto.AccountAllocationTemplateDTO;
import com.testApplication.dto.AccountAllocationTemplateDTO.AccountAllocationDetails;
import com.testApplication.exception.AllocationTemplateException;
import com.testApplication.mapper.AccountAllocationTemplateMapper;
import com.testApplication.model.Account;
import com.testApplication.model.AccountAllocationTemplate;
import com.testApplication.model.AccountAllocationTemplateAccount;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.AccountAllocationTemplateRepository;
import com.testApplication.repository.AccountRepository;
import com.testApplication.repository.LegalEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountAllocationTemplateServiceTest {

    @Mock
    private AccountAllocationTemplateRepository templateRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LegalEntityRepository legalEntityRepository;

    @Mock
    private AccountAllocationTemplateMapper templateMapper;

    @Mock
    private SecurityService securityService;

    private AccountAllocationTemplateService templateService;

    private AccountAllocationTemplate testTemplate;
    private AccountAllocationTemplateDTO testTemplateDTO;
    private Account testAccount;
    private LegalEntity testLegalEntity;
    private AccountAllocationTemplateAccount testTemplateAccount;
    private Set<AccountAllocationTemplateAccount> testTemplateAccounts;
    private Set<AccountAllocationTemplateDTO.AccountAllocationDetails> testAccountDetails;

    @BeforeEach
    void setUp() {
        // Set up security context with test user
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test-user");
        when(authentication.getPrincipal()).thenReturn(authentication);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        // Allow all legal entity access in tests by default
        when(securityService.hasAccessToLegalEntity(any(org.springframework.security.core.userdetails.UserDetails.class), anyLong())).thenReturn(true);

        templateService = new AccountAllocationTemplateService(
            templateRepository, 
            accountRepository,
            legalEntityRepository,
            templateMapper);

        // Set up test data
        testLegalEntity = new LegalEntity();
        testLegalEntity.setId(1L);
        testLegalEntity.setName("Test Legal Entity");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setCode("TEST001");
        testAccount.setName("Test Account");
        testAccount.setLegalEntity(testLegalEntity);

        testTemplate = new AccountAllocationTemplate();
        testTemplate.setId(1L);
        testTemplate.setCode("TEMPLATE001");
        testTemplate.setName("Test Template");
        testTemplate.setDescription("Test Description");
        testTemplate.setLegalEntity(testLegalEntity);
        
        testTemplateAccount = new AccountAllocationTemplateAccount();
        testTemplateAccount.setTemplate(testTemplate);
        testTemplateAccount.setAccount(testAccount);
        testTemplateAccount.setAllocationOrder(1);
        testTemplateAccount.setIsSource(true);

        testTemplateAccounts = new HashSet<>();
        testTemplateAccounts.add(testTemplateAccount);
        testTemplate.setTemplateAccounts(testTemplateAccounts);

        testAccountDetails = new HashSet<>();
        testAccountDetails.add(AccountAllocationTemplateDTO.AccountAllocationDetails.builder()
                .accountId(1L)
                .accountCode("TEST001")
                .allocationOrder(1)
                .isSource(true)
                .build());

        testTemplateDTO = AccountAllocationTemplateDTO.builder()
                .id(1L)
                .code("TEMPLATE001")
                .name("Test Template")
                .description("Test Description")
                .legalEntityId(1L)
                .legalEntityName("Test Legal Entity")
                .allocation_details(testAccountDetails)
                .build();
    }

    @Test
    void getAllTemplates_ShouldReturnAllTemplates() {
        // Arrange
        when(templateRepository.findAll()).thenReturn(List.of(testTemplate));
        when(templateMapper.toDTOList(any())).thenReturn(List.of(testTemplateDTO));

        // Act
        List<AccountAllocationTemplateDTO> result = templateService.getAllTemplates();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplateDTO, result.get(0));
        verify(templateRepository).findAll();
        verify(templateMapper).toDTOList(any());
    }

    @Test
    void getTemplateById_WhenExists_ShouldReturnTemplate() {
        // Arrange
        when(templateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(templateMapper.toDTO(testTemplate)).thenReturn(testTemplateDTO);

        // Act
        Optional<AccountAllocationTemplateDTO> result = templateService.getTemplateById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTemplateDTO, result.get());
        assertEquals("Test Legal Entity", result.get().getLegalEntityName());
        verify(templateRepository).findById(1L);
        verify(templateMapper).toDTO(testTemplate);
    }

    @Test
    void getTemplateById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(templateRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<AccountAllocationTemplateDTO> result = templateService.getTemplateById(99L);

        // Assert
        assertTrue(result.isEmpty());
        verify(templateRepository).findById(99L);
        verify(templateMapper, never()).toDTO(any());
    }

    @Test
    void createTemplate_WithValidData_ShouldCreateTemplate() {
        // Arrange
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountRepository.findByCodeAndLegalEntity_Id("TEST001", 1L)).thenReturn(Optional.of(testAccount));
        when(templateRepository.save(any(AccountAllocationTemplate.class))).thenReturn(testTemplate);
        when(templateMapper.toDTO(testTemplate)).thenReturn(testTemplateDTO);

        // Act
        AccountAllocationTemplateDTO result = templateService.createTemplate(testTemplateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testTemplateDTO.getCode(), result.getCode());
        assertEquals(testTemplateDTO.getName(), result.getName());
        assertEquals(testTemplateDTO.getLegalEntityName(), result.getLegalEntityName());
        verify(templateRepository).save(any(AccountAllocationTemplate.class));
        verify(templateMapper).toDTO(testTemplate);
    }

    @Test
    void createTemplate_WithInvalidLegalEntity_ShouldThrowException() {
        // Arrange
        when(legalEntityRepository.findById(99L)).thenReturn(Optional.empty());
        testTemplateDTO.setLegalEntityId(99L);

        // Act & Assert
        assertThrows(AllocationTemplateException.LegalEntityNotFoundException.class, () -> 
            templateService.createTemplate(testTemplateDTO)
        );
        verify(templateRepository, never()).save(any(AccountAllocationTemplate.class));
        verify(templateMapper, never()).toDTO(any());
    }

    @Test
    void createTemplate_WithInvalidAccount_ShouldThrowException() {
        // Arrange
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountRepository.findByCodeAndLegalEntity_Id("INVALID", 1L)).thenReturn(Optional.empty());
        
        testAccountDetails.clear();
        testAccountDetails.add(AccountAllocationTemplateDTO.AccountAllocationDetails.builder()
                .accountCode("INVALID")
                .allocationOrder(1)
                .isSource(true)
                .build());

        // Act & Assert
        assertThrows(AllocationTemplateException.AccountNotFoundException.class, () -> 
            templateService.createTemplate(testTemplateDTO)
        );
        verify(templateRepository, never()).save(any(AccountAllocationTemplate.class));
        verify(templateMapper, never()).toDTO(any());
    }

    @Test
    void updateTemplate_WithValidData_ShouldUpdateTemplate() {
        // Arrange
        when(templateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(legalEntityRepository.findById(1L)).thenReturn(Optional.of(testLegalEntity));
        when(accountRepository.findByCodeAndLegalEntity_Id("TEST001", 1L)).thenReturn(Optional.of(testAccount));
        when(templateRepository.save(any(AccountAllocationTemplate.class))).thenReturn(testTemplate);
        when(templateMapper.toDTO(testTemplate)).thenReturn(testTemplateDTO);

        // Act
        AccountAllocationTemplateDTO result = templateService.updateTemplate(1L, testTemplateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testTemplateDTO.getCode(), result.getCode());
        assertEquals(testTemplateDTO.getName(), result.getName());
        assertEquals(testTemplateDTO.getLegalEntityName(), result.getLegalEntityName());
        verify(templateRepository).save(any(AccountAllocationTemplate.class));
        verify(templateMapper).toDTO(testTemplate);
    }

    @Test
    void updateTemplate_WithInvalidTemplate_ShouldThrowException() {
        // Arrange
        when(templateRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AllocationTemplateException.InvalidTemplateException.class, () -> 
            templateService.updateTemplate(99L, testTemplateDTO)
        );
        verify(templateRepository, never()).save(any(AccountAllocationTemplate.class));
        verify(templateMapper, never()).toDTO(any());
    }

    @Test
    void updateTemplate_WithInvalidAccount_ShouldThrowException() {
        // Arrange
        when(templateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));
        when(accountRepository.findByCodeAndLegalEntity_Id("INVALID", 1L)).thenReturn(Optional.empty());
        
        testAccountDetails.clear();
        testAccountDetails.add(AccountAllocationTemplateDTO.AccountAllocationDetails.builder()
                .accountCode("INVALID")
                .allocationOrder(1)
                .isSource(true)
                .build());

        // Act & Assert
        assertThrows(AllocationTemplateException.AccountNotFoundException.class, () -> 
            templateService.updateTemplate(1L, testTemplateDTO)
        );
        verify(templateRepository, never()).save(any(AccountAllocationTemplate.class));
        verify(templateMapper, never()).toDTO(any());
    }

    @Test
    void getTemplatesByType_ShouldReturnTemplatesWithMatchingType() {
        // Arrange
        when(templateRepository.findByTemplateAccounts_IsSource(true))
                .thenReturn(List.of(testTemplate));
        when(templateMapper.toDTO(testTemplate)).thenReturn(testTemplateDTO);

        // Act
        List<AccountAllocationTemplateDTO> result = templateService.getTemplatesByType(true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplateDTO.getLegalEntityName(), result.get(0).getLegalEntityName());
        verify(templateRepository).findByTemplateAccounts_IsSource(true);
    }

    @Test
    void getTemplatesByLegalEntity_ShouldReturnTemplatesForLegalEntity() {
        // Arrange
        when(templateRepository.findByLegalEntity_Id(1L))
                .thenReturn(List.of(testTemplate));
        when(templateMapper.toDTOList(anyList())).thenReturn(List.of(testTemplateDTO));

        // Act
        List<AccountAllocationTemplateDTO> result = templateService.getTemplatesByLegalEntity(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTemplateDTO.getLegalEntityId(), result.get(0).getLegalEntityId());
        assertEquals(testTemplateDTO.getLegalEntityName(), result.get(0).getLegalEntityName());
        verify(templateRepository).findByLegalEntity_Id(1L);
        verify(templateMapper).toDTOList(anyList());
    }
}
