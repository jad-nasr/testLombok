package com.testApplication.service;

import com.testApplication.dto.TransactionDTO;
import com.testApplication.model.Transaction;
import com.testApplication.model.Customer;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.TransactionRepository;
import com.testApplication.repository.CustomerRepository;
import com.testApplication.repository.LegalEntityRepository;
import com.testApplication.mapper.TransactionMapper;
import com.testApplication.security.RequiresLegalEntityAccess;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final LegalEntityRepository legalEntityRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            CustomerRepository customerRepository,
            LegalEntityRepository legalEntityRepository,
            TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.legalEntityRepository = legalEntityRepository;
        this.transactionMapper = transactionMapper;
    }

    public List<TransactionDTO> getAllTransactions() {
        return transactionMapper.toDTOList(transactionRepository.findAll());
    }

    public Optional<TransactionDTO> getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDTO);
    }

    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    public Optional<TransactionDTO> findByTransactionCodeAndLegalEntity(String transactionCode, Long legalEntityId) {
        return transactionRepository.findByTransactionCodeAndLegalEntity_Id(transactionCode, legalEntityId)
                .map(transactionMapper::toDTO);
    }

    @RequiresLegalEntityAccess(legalEntityIdParam = "dto.legalEntityId")
    @Transactional
    public TransactionDTO createTransaction(TransactionDTO dto, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
                
        LegalEntity legalEntity = legalEntityRepository.findById(dto.getLegalEntityId())
                .orElseThrow(() -> new RuntimeException("Legal Entity not found"));        // Check if transaction already exists
        Optional<Transaction> existingTransaction = transactionRepository.findByTransactionCodeAndLegalEntity_Id(dto.getTransactionCode(), dto.getLegalEntityId());
        
        if (existingTransaction.isPresent()) {
            throw new RuntimeException("Transaction already exists for this legal entity");
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        Transaction transaction = Transaction.builder()
                .transactionCode(dto.getTransactionCode())
                .transactionType(dto.getTransactionType())
                .date(dto.getDate())
                .description(dto.getDescription())
                .approvalStatus(dto.getApprovalStatus())
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .customer(customer)
                .legalEntity(legalEntity)
                .createdBy(currentUser)
                .createdAt(Instant.now())
                .updatedBy(currentUser)
                .updatedAt(Instant.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    @RequiresLegalEntityAccess(legalEntityIdParam = "dto.legalEntityId")
    @Transactional
    public TransactionDTO updateTransaction(Long id, TransactionDTO dto) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        existing.setTransactionCode(dto.getTransactionCode());
        existing.setTransactionType(dto.getTransactionType());
        existing.setDate(dto.getDate());
        existing.setDescription(dto.getDescription());
        existing.setApprovalStatus(dto.getApprovalStatus());
        existing.setAmount(dto.getAmount());
        existing.setCurrency(dto.getCurrency());

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setUpdatedBy(currentUser);
        existing.setUpdatedAt(Instant.now());

        Transaction updated = transactionRepository.save(existing);
        return transactionMapper.toDTO(updated);
    }    @Transactional
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    public List<TransactionDTO> getTransactionsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return transactionMapper.toDTOList(transactionRepository.findByCustomer(customer));
    }    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    public List<TransactionDTO> getTransactionsByLegalEntity(Long legalEntityId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new RuntimeException("Legal Entity not found");
        }
        return transactionMapper.toDTOList(transactionRepository.findByLegalEntity_Id(legalEntityId));
    }
}