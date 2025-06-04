package com.testApplication.controller;

import com.testApplication.model.Transaction;
import com.testApplication.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody Transaction transaction,
            @RequestParam Long customerId,
            @RequestParam Long legalEntityId) {
        Transaction created = transactionService.createTransaction(transaction, customerId, legalEntityId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transaction) {
        try {
            Transaction updated = transactionService.updateTransaction(id, transaction);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCustomer(@PathVariable Long customerId) {
        List<Transaction> transactions = transactionService.getTransactionsByCustomer(customerId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/by-legal-entity/{legalEntityId}")
    public ResponseEntity<List<Transaction>> getTransactionsByLegalEntity(@PathVariable Long legalEntityId) {
        List<Transaction> transactions = transactionService.getTransactionsByLegalEntity(legalEntityId);
        return ResponseEntity.ok(transactions);
    }
}
