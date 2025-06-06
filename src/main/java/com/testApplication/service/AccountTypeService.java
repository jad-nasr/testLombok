package com.testApplication.service;

import com.testApplication.dto.AccountTypeDTO;
import com.testApplication.mapper.AccountTypeMapper;
import com.testApplication.model.AccountType;
import com.testApplication.model.AccountCategory;
import com.testApplication.repository.AccountTypeRepository;
import com.testApplication.repository.AccountCategoryRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountTypeService {

    private final AccountTypeRepository accountTypeRepository;
    private final AccountTypeMapper accountTypeMapper;
    private final AccountCategoryRepository accountCategoryRepository;

    public AccountTypeService(
            AccountTypeRepository accountTypeRepository,
            AccountTypeMapper accountTypeMapper,
            AccountCategoryRepository accountCategoryRepository) {
        this.accountTypeRepository = accountTypeRepository;
        this.accountTypeMapper = accountTypeMapper;
        this.accountCategoryRepository = accountCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountTypeDTO> getAllAccountTypes() {
        return accountTypeMapper.toDTOList(accountTypeRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<AccountTypeDTO> getAccountTypeById(Long id) {
        return accountTypeRepository.findById(id)
                .map(accountTypeMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<AccountTypeDTO> getAccountTypeByCode(String code) {
        return accountTypeRepository.findByCode(code)
                .map(accountTypeMapper::toDTO);
    }    public AccountTypeDTO createAccountType(AccountTypeDTO accountTypeDTO) {
        AccountType accountType = accountTypeMapper.toEntity(accountTypeDTO);
        
        if (accountTypeDTO.getAccountCategoryId() != null) {
            AccountCategory category = accountCategoryRepository.findById(accountTypeDTO.getAccountCategoryId())
                .orElseThrow(() -> new RuntimeException("Account category not found with id: " + accountTypeDTO.getAccountCategoryId()));
            accountType.setAccountCategory(category);
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        accountType.setCreatedBy(currentUser);
        accountType.setCreatedAt(Instant.now());
        accountType.setUpdatedBy(currentUser);
        accountType.setUpdatedAt(Instant.now());
        
        AccountType savedType = accountTypeRepository.save(accountType);
        return accountTypeMapper.toDTO(savedType);
    }

    public AccountTypeDTO updateAccountType(Long id, AccountTypeDTO accountTypeDTO) {
        AccountType accountType = accountTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account type not found with id: " + id));
        
        accountType.setCode(accountTypeDTO.getCode());
        accountType.setName(accountTypeDTO.getName());
        accountType.setDescription(accountTypeDTO.getDescription());
        
        if (accountTypeDTO.getAccountCategoryId() != null) {
            AccountCategory category = accountCategoryRepository.findById(accountTypeDTO.getAccountCategoryId())
                .orElseThrow(() -> new RuntimeException("Account category not found with id: " + accountTypeDTO.getAccountCategoryId()));
            accountType.setAccountCategory(category);
        } else {
            accountType.setAccountCategory(null);
        }
        
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        accountType.setUpdatedBy(currentUser);
        accountType.setUpdatedAt(Instant.now());
        
        AccountType updatedType = accountTypeRepository.save(accountType);
        return accountTypeMapper.toDTO(updatedType);
    }

    public void deleteAccountType(Long id) {
        accountTypeRepository.deleteById(id);
    }
}
