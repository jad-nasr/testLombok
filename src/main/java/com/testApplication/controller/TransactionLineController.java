package com.testApplication.controller;

import com.testApplication.model.TransactionLine;
import com.testApplication.service.TransactionLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-lines")
public class TransactionLineController {

    private final TransactionLineService transactionLineService;

    @Autowired
    public TransactionLineController(TransactionLineService transactionLineService) {
        this.transactionLineService = transactionLineService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionLine>> getAllTransactionLines() {
        return ResponseEntity.ok(transactionLineService.getAllTransactionLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionLine> getTransactionLineById(@PathVariable Long id) {
        return transactionLineService.getTransactionLineById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionLine> createTransactionLine(
            @RequestBody TransactionLine transactionLine,
            @RequestParam Long transactionId,
            @RequestParam Long accountId) {
        TransactionLine created = transactionLineService.createTransactionLine(transactionLine, transactionId, accountId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionLine> updateTransactionLine(
            @PathVariable Long id,
            @RequestBody TransactionLine transactionLine) {
        try {
            TransactionLine updated = transactionLineService.updateTransactionLine(id, transactionLine);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionLine(@PathVariable Long id) {
        try {
            transactionLineService.deleteTransactionLine(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-transaction/{transactionId}")
    public ResponseEntity<List<TransactionLine>> getTransactionLinesByTransaction(@PathVariable Long transactionId) {
        List<TransactionLine> transactionLines = transactionLineService.getTransactionLinesByTransaction(transactionId);
        return ResponseEntity.ok(transactionLines);
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<TransactionLine>> getTransactionLinesByAccount(@PathVariable Long accountId) {
        List<TransactionLine> transactionLines = transactionLineService.getTransactionLinesByAccount(accountId);
        return ResponseEntity.ok(transactionLines);
    }
}
