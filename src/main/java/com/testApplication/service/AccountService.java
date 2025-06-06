package com.testApplication.service;

import com.testApplication.dto.AccountDTO;
import com.testApplication.mapper.AccountMapper;
import com.testApplication.model.Account;
import com.testApplication.model.AccountType;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.AccountRepository;
import com.testApplication.repository.AccountTypeRepository;
import com.testApplication.repository.LegalEntityRepository;
import com.testApplication.security.RequiresLegalEntityAccess;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final LegalEntityRepository legalEntityRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository,
                        AccountTypeRepository accountTypeRepository,
                        LegalEntityRepository legalEntityRepository,
                        AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.legalEntityRepository = legalEntityRepository;
        this.accountMapper = accountMapper;
    }

    @RequiresLegalEntityAccess
    public AccountDTO createAccount(Long legalEntityId, Long accountTypeId, Long parentAccountId, AccountDTO accountDTO) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
        AccountType accountType = accountTypeRepository.findById(accountTypeId)
                .orElseThrow(() -> new RuntimeException("Account type not found with id: " + accountTypeId));
                
        Account account = accountMapper.toEntity(accountDTO);
        account.setLegalEntity(legalEntity);
        account.setAccountType(accountType);

        if (parentAccountId != null) {
            Account parentAccount = accountRepository.findById(parentAccountId)
                    .orElseThrow(() -> new RuntimeException("Parent account not found with id: " + parentAccountId));
            account.setParentAccount(parentAccount);
        } else {
            account.setParentAccount(null);
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        account.setCreatedBy(currentUser);
        account.setCreatedAt(Instant.now());
        account.setUpdatedBy(currentUser);
        account.setUpdatedAt(Instant.now());

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDTO(savedAccount);
    }

    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    @Transactional(readOnly = true)
    public Optional<AccountDTO> getAccountById(Long legalEntityId, Long id) {
        return accountRepository.findById(id)
            .filter(account -> account.getLegalEntity().getId().equals(legalEntityId))
            .map(accountMapper::toDTO);
    }    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts(Long legalEntityId) {
        return accountRepository.findByLegalEntity_Id(legalEntityId).stream()
            .map(accountMapper::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }

    @RequiresLegalEntityAccess
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByLegalEntity(Long legalEntityId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new RuntimeException("Legal entity not found with id: " + legalEntityId);
        }
        return accountRepository.findByLegalEntity_Id(legalEntityId).stream()
            .map(accountMapper::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }    @RequiresLegalEntityAccess
    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByParentAccount(Long legalEntityId, Long parentAccountId) {
        Account parentAccount = accountRepository.findById(parentAccountId)
                .orElseThrow(() -> new RuntimeException("Parent account not found with id: " + parentAccountId));
                
        // Add explicit legal entity check
        if (!parentAccount.getLegalEntity().getId().equals(legalEntityId)) {
            throw new RuntimeException("Parent account does not belong to legal entity: " + legalEntityId);
        }
        
        return accountRepository.findByParentAccount(parentAccount)
                .stream()
                .filter(account -> account.getLegalEntity().getId().equals(legalEntityId))
                .map(accountMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @RequiresLegalEntityAccess
    public AccountDTO updateAccount(Long legalEntityId, Long id, AccountDTO updatedDTO, Long accountTypeId, Long parentAccountId) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
                
        // Add explicit legal entity check
        if (!account.getLegalEntity().getId().equals(legalEntityId)) {
            throw new RuntimeException("Account does not belong to legal entity: " + legalEntityId);
        }

        // Update account with DTO values
        account.setCode(updatedDTO.getCode());
        account.setName(updatedDTO.getName());
        account.setDescription(updatedDTO.getDescription());
        account.setActive(updatedDTO.isActive());

        if (accountTypeId != null) {
            AccountType accountType = accountTypeRepository.findById(accountTypeId)
                    .orElseThrow(() -> new RuntimeException("Account type not found with id: " + accountTypeId));
            account.setAccountType(accountType);
        }

        if (parentAccountId != null) {
            Account parentAccount = accountRepository.findById(parentAccountId)
                    .orElseThrow(() -> new RuntimeException("Parent account not found with id: " + parentAccountId));
            account.setParentAccount(parentAccount);
        } else {
            account.setParentAccount(null);
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        account.setUpdatedBy(currentUser);
        account.setUpdatedAt(Instant.now());

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDTO(savedAccount);
    }
    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    public void deleteAccount(Long legalEntityId, Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        
        if (!account.getLegalEntity().getId().equals(legalEntityId)) {
            throw new RuntimeException("Account does not belong to legal entity: " + legalEntityId);
        }
        
        accountRepository.deleteById(id);
    }
}