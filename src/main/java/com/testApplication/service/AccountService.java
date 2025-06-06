package com.testApplication.service;

import com.testApplication.model.Account;
import com.testApplication.model.AccountType;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.AccountRepository;
import com.testApplication.repository.AccountTypeRepository;
import com.testApplication.repository.LegalEntityRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final LegalEntityRepository legalEntityRepository;

    public AccountService(AccountRepository accountRepository,
                          AccountTypeRepository accountTypeRepository,
                          LegalEntityRepository legalEntityRepository) {
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.legalEntityRepository = legalEntityRepository;
    }

    public Account createAccount(Long legalEntityId, Long accountTypeId, Long parentAccountId, Account account) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
        AccountType accountType = accountTypeRepository.findById(accountTypeId)
                .orElseThrow(() -> new RuntimeException("Account type not found with id: " + accountTypeId));
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

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByLegalEntity(Long legalEntityId) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
        return accountRepository.findByLegalEntity(legalEntity);
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByParentAccount(Long parentAccountId) {
        Account parentAccount = accountRepository.findById(parentAccountId)
                .orElseThrow(() -> new RuntimeException("Parent account not found with id: " + parentAccountId));
        return accountRepository.findByParentAccount(parentAccount);
    }

    public Account updateAccount(Long id, Account updated, Long accountTypeId, Long parentAccountId) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        account.setCode(updated.getCode());
        account.setName(updated.getName());
        account.setDescription(updated.getDescription());
        account.setActive(updated.isActive());

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

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}