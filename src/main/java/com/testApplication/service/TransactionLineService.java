package com.testApplication.service;

import com.testApplication.model.TransactionLine;
import com.testApplication.model.Transaction;
import com.testApplication.model.Account;
import com.testApplication.repository.TransactionLineRepository;
import com.testApplication.repository.TransactionRepository;
import com.testApplication.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionLineService {

    private final TransactionLineRepository transactionLineRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionLineService(TransactionLineRepository transactionLineRepository,
                                 TransactionRepository transactionRepository,
                                 AccountRepository accountRepository) {
        this.transactionLineRepository = transactionLineRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public TransactionLine createTransactionLine(TransactionLine transactionLine, Long transactionId, Long accountId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        transactionLine.setTransaction(transaction);
        transactionLine.setAccount(account);
        return transactionLineRepository.save(transactionLine);
    }

    public Optional<TransactionLine> getTransactionLineById(Long id) {
        return transactionLineRepository.findById(id);
    }

    public List<TransactionLine> getAllTransactionLines() {
        return transactionLineRepository.findAll();
    }

    public List<TransactionLine> getTransactionLinesByTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        return transactionLineRepository.findByTransaction(transaction);
    }

    public List<TransactionLine> getTransactionLinesByAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        return transactionLineRepository.findByAccount(account);
    }

    public TransactionLine updateTransactionLine(Long id, TransactionLine updated) {
        TransactionLine transactionLine = transactionLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TransactionLine not found with id: " + id));
        transactionLine.setAmount(updated.getAmount());
        transactionLine.setDescription(updated.getDescription());
        transactionLine.setDebit(updated.isDebit());
        // Optionally update transaction/account if needed
        return transactionLineRepository.save(transactionLine);
    }

    public void deleteTransactionLine(Long id) {
        transactionLineRepository.deleteById(id);
    }
}