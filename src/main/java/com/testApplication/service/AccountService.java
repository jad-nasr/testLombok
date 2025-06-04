package com.testApplication.service;

import com.testApplication.model.Account;
import com.testApplication.model.AccountType;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.AccountRepository;
import com.testApplication.repository.AccountTypeRepository;
import com.testApplication.repository.LegalEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final LegalEntityRepository legalEntityRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          AccountTypeRepository accountTypeRepository,
                          LegalEntityRepository legalEntityRepository) {
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.legalEntityRepository = legalEntityRepository;
    }

    public Account createAccount(Long legalEntityId, Long accountTypeId, Account account) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
        AccountType accountType = accountTypeRepository.findById(accountTypeId)
                .orElseThrow(() -> new RuntimeException("Account type not found with id: " + accountTypeId));
        account.setLegalEntity(legalEntity);
        account.setAccountType(accountType);
        return accountRepository.save(account);
    }

    public List<Account> getAccountsForLegalEntity(Long legalEntityId) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
        return accountRepository.findByLegalEntity(legalEntity);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Account updateAccount(Long id, Account updated, Long accountTypeId) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
        account.setName(updated.getName());
        account.setCode(updated.getCode());
        account.setDescription(updated.getDescription());
        account.setActive(updated.isActive());
        if (accountTypeId != null) {
            AccountType accountType = accountTypeRepository.findById(accountTypeId)
                    .orElseThrow(() -> new RuntimeException("Account type not found with id: " + accountTypeId));
            account.setAccountType(accountType);
        }
        // Changing legal entity is not allowed here; add a method if you want to enable that
        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}