package com.testApplication.service;

import com.testApplication.model.AccountCategory;
import com.testApplication.repository.AccountCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountCategoryService {

    private final AccountCategoryRepository accountCategoryRepository;

    public AccountCategoryService(AccountCategoryRepository accountCategoryRepository) {
        this.accountCategoryRepository = accountCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountCategory> getAllAccountCategories() {
        return accountCategoryRepository.findAllByOrderByCodeAsc();
    }

    @Transactional(readOnly = true)
    public Optional<AccountCategory> getAccountCategoryById(Long id) {
        return accountCategoryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<AccountCategory> getAccountCategoryByCode(String code) {
        return accountCategoryRepository.findByCode(code);
    }

    public AccountCategory createAccountCategory(AccountCategory accountCategory) {
        if (accountCategoryRepository.existsByCode(accountCategory.getCode())) {
            throw new RuntimeException("Account category already exists with code: " + accountCategory.getCode());
        }
        return accountCategoryRepository.save(accountCategory);
    }

    public AccountCategory updateAccountCategory(Long id, AccountCategory updated) {
        AccountCategory accountCategory = accountCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account category not found with id: " + id));

        // Check if code is being changed and if new code already exists
        if (!accountCategory.getCode().equals(updated.getCode()) &&
            accountCategoryRepository.existsByCode(updated.getCode())) {
            throw new RuntimeException("Account category already exists with code: " + updated.getCode());
        }

        accountCategory.setCode(updated.getCode());
        accountCategory.setName(updated.getName());
        accountCategory.setDescription(updated.getDescription());

        return accountCategoryRepository.save(accountCategory);
    }

    public void deleteAccountCategory(Long id) {
        if (!accountCategoryRepository.existsById(id)) {
            throw new RuntimeException("Account category not found with id: " + id);
        }
        accountCategoryRepository.deleteById(id);
    }
}