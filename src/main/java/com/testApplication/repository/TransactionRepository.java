package com.testApplication.repository;

import com.testApplication.model.Transaction;
import com.testApplication.model.Customer;
import com.testApplication.model.LegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomer(Customer customer);
    List<Transaction> findByCustomerId(Long customerId);
    List<Transaction> findByLegalEntity(LegalEntity legalEntity);
    List<Transaction> findByLegalEntityId(Long legalEntityId);
    Optional<Transaction> findByTransactionCodeAndLegalEntity_Id(String transactionCode, Long legalEntityId);
}