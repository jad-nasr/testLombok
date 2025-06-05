package com.testApplication.repository;

import com.testApplication.model.Account;
import com.testApplication.model.LegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCodeAndLegalEntity(String code, LegalEntity legalEntity);
    List<Account> findByParentAccount(Account parentAccount);
    List<Account> findByLegalEntity(LegalEntity legalEntity); // Added method
    Optional<Account> findByCodeAndLegalEntityId(String code, Long legalEntityId);
}