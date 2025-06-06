package com.testApplication.service;

import com.testApplication.security.RequiresLegalEntityAccess;
import com.testApplication.dto.AccountAllocationTemplateDTO;
import com.testApplication.dto.AccountAllocationTemplateDTO.AccountAllocationDetails;
import com.testApplication.exception.AllocationTemplateException;
import com.testApplication.mapper.AccountAllocationTemplateMapper;
import com.testApplication.model.AccountAllocationTemplate;
import com.testApplication.model.AccountAllocationTemplateAccount;
import com.testApplication.model.Account;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.AccountAllocationTemplateRepository;
import com.testApplication.repository.AccountRepository;
import com.testApplication.repository.LegalEntityRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountAllocationTemplateService {

    private final AccountAllocationTemplateRepository templateRepository;
    private final AccountRepository accountRepository;
    private final LegalEntityRepository legalEntityRepository;
    private final AccountAllocationTemplateMapper templateMapper;

    public AccountAllocationTemplateService(
            AccountAllocationTemplateRepository templateRepository,
            AccountRepository accountRepository,
            LegalEntityRepository legalEntityRepository,
            AccountAllocationTemplateMapper templateMapper) {
        this.templateRepository = templateRepository;
        this.accountRepository = accountRepository;
        this.legalEntityRepository = legalEntityRepository;
        this.templateMapper = templateMapper;
    }

    @Transactional(readOnly = true)
    public List<AccountAllocationTemplateDTO> getAllTemplates() {
        return templateMapper.toDTOList(templateRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<AccountAllocationTemplateDTO> getTemplateById(Long id) {
        return templateRepository.findById(id)
                .map(templateMapper::toDTO);
    }    @Transactional(readOnly = true)
    public Optional<AccountAllocationTemplateDTO> getTemplateByCode(String code) {
        return templateRepository.findByCodeAndLegalEntity_Id(code, null)
                .map(templateMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<AccountAllocationTemplateDTO> getTemplatesByType(Boolean isSource) {
        return templateRepository.findByTemplateAccounts_IsSource(isSource).stream()
                .map(templateMapper::toDTO)
                .collect(Collectors.toList());
    }    @RequiresLegalEntityAccess(legalEntityIdParam = "dto.legalEntityId")
    public AccountAllocationTemplateDTO createTemplate(AccountAllocationTemplateDTO dto) {
        if (dto.getLegalEntityId() == null) {
            throw new AllocationTemplateException.InvalidTemplateException("Legal entity ID is required");
        }

        LegalEntity legalEntity = legalEntityRepository.findById(dto.getLegalEntityId())
                .orElseThrow(() -> new AllocationTemplateException.LegalEntityNotFoundException(dto.getLegalEntityId()));

        if (templateRepository.existsByCodeAndLegalEntity_Id(dto.getCode(), dto.getLegalEntityId())) {
            throw new AllocationTemplateException.DuplicateTemplateException(dto.getCode(), dto.getLegalEntityId());
        }String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AccountAllocationTemplate template = AccountAllocationTemplate.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .legalEntity(legalEntity)
                .templateAccounts(new HashSet<>())
                .createdAt(Instant.now())
                .createdBy(currentUser)
                .build();

        // Add template accounts
        if (dto.getAllocation_details() != null) {
            for (AccountAllocationDetails accountDetails : dto.getAllocation_details()) {                if (accountDetails.getAccountCode() == null || accountDetails.getAccountCode().trim().isEmpty()) {
                    throw new AllocationTemplateException.InvalidTemplateException("Account code is required for all allocation details");
                }

                Account account = accountRepository.findByCodeAndLegalEntity_Id(accountDetails.getAccountCode(), dto.getLegalEntityId())
                        .orElseThrow(() -> new AllocationTemplateException.AccountNotFoundException(accountDetails.getAccountCode(), dto.getLegalEntityId()));

                AccountAllocationTemplateAccount templateAccount = AccountAllocationTemplateAccount.builder()
                        .template(template)
                        .account(account)
                        .allocationOrder(accountDetails.getAllocationOrder())
                        .isSource(accountDetails.getIsSource())
                        .build();

                template.getTemplateAccounts().add(templateAccount);
            }
        }

        return templateMapper.toDTO(templateRepository.save(template));
    }    @RequiresLegalEntityAccess
    public AccountAllocationTemplateDTO updateTemplate(Long id, AccountAllocationTemplateDTO dto) {
        AccountAllocationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new AllocationTemplateException.InvalidTemplateException("Template not found with id: " + id));

        // First validate all account codes exist
        if (dto.getAllocation_details() != null) {
            Long legalEntityId = dto.getLegalEntityId() != null ? dto.getLegalEntityId() : template.getLegalEntity().getId();
            for (AccountAllocationTemplateDTO.AccountAllocationDetails accountDetails : dto.getAllocation_details()) {
                if (accountDetails.getAccountCode() != null) {
                    if (!accountRepository.findByCodeAndLegalEntity_Id(accountDetails.getAccountCode(), legalEntityId).isPresent()) {
                        throw new AllocationTemplateException.AccountNotFoundException(accountDetails.getAccountCode(), legalEntityId);
                    }
                }
            }
        }

        if (dto.getLegalEntityId() != null) {
            LegalEntity legalEntity = legalEntityRepository.findById(dto.getLegalEntityId())
                    .orElseThrow(() -> new AllocationTemplateException.LegalEntityNotFoundException(dto.getLegalEntityId()));

            // If legal entity is changing, check code uniqueness in new legal entity
            if (!template.getLegalEntity().getId().equals(dto.getLegalEntityId()) &&
                templateRepository.existsByCodeAndLegalEntity_Id(template.getCode(), dto.getLegalEntityId())) {
                throw new AllocationTemplateException.DuplicateTemplateException(template.getCode(), dto.getLegalEntityId());
            }

            template.setLegalEntity(legalEntity);
        }

        template.setName(dto.getName());
        template.setDescription(dto.getDescription());String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        template.setUpdatedAt(Instant.now());
        template.setUpdatedBy(currentUser);

        // Clear existing account associations
        template.getTemplateAccounts().clear();

        // Add new account associations
        if (dto.getAllocation_details() != null) {
            for (AccountAllocationDetails accountDetails : dto.getAllocation_details()) {                if (accountDetails.getAccountCode() == null || accountDetails.getAccountCode().trim().isEmpty()) {
                    throw new AllocationTemplateException.InvalidTemplateException("Account code is required for all allocation details");
                }

                Long legalEntityId = dto.getLegalEntityId() != null ? dto.getLegalEntityId() : template.getLegalEntity().getId();
                Account account = accountRepository.findByCodeAndLegalEntity_Id(accountDetails.getAccountCode(), legalEntityId)
                        .orElseThrow(() -> new AllocationTemplateException.AccountNotFoundException(accountDetails.getAccountCode(), legalEntityId));

                AccountAllocationTemplateAccount templateAccount = AccountAllocationTemplateAccount.builder()
                        .template(template)
                        .account(account)
                        .allocationOrder(accountDetails.getAllocationOrder())
                        .isSource(accountDetails.getIsSource())
                        .build();

                template.getTemplateAccounts().add(templateAccount);
            }
        }

        return templateMapper.toDTO(templateRepository.save(template));
    }

    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }

    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    @Transactional(readOnly = true)
    public List<AccountAllocationTemplateDTO> getTemplatesByLegalEntity(Long legalEntityId) {
        return templateMapper.toDTOList(templateRepository.findByLegalEntity_Id(legalEntityId));
    }
}
