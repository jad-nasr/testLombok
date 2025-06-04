package com.testApplication.service;

import com.testApplication.model.Transaction;
import com.testApplication.model.Customer;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.TransactionRepository;
import com.testApplication.repository.CustomerRepository;
import com.testApplication.repository.LegalEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final LegalEntityRepository legalEntityRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                             CustomerRepository customerRepository,
                             LegalEntityRepository legalEntityRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.legalEntityRepository = legalEntityRepository;
    }

    public Transaction createTransaction(Transaction transaction, Long customerId, Long legalEntityId) {
        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
            transaction.setCustomer(customer);
        }
        if (legalEntityId != null) {
            LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                    .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
            transaction.setLegalEntity(legalEntity);
        }
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        return transactionRepository.findByCustomer(customer);
    }

    public List<Transaction> getTransactionsByLegalEntity(Long legalEntityId) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
        return transactionRepository.findByLegalEntity(legalEntity);
    }

    public Transaction updateTransaction(Long id, Transaction updated) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        transaction.setTransactionType(updated.getTransactionType());
        transaction.setDate(updated.getDate());
        transaction.setAmount(updated.getAmount());
        transaction.setCurrency(updated.getCurrency());
        transaction.setDescription(updated.getDescription());
        transaction.setApprovalStatus(updated.getApprovalStatus());
        transaction.setCreatedAt(updated.getCreatedAt());
        transaction.setUpdatedAt(updated.getUpdatedAt());
        transaction.setCreatedBy(updated.getCreatedBy());
        // Optionally update customer and legalEntity if needed
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}