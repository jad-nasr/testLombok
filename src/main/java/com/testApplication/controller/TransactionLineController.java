package com.testApplication.controller;

import com.testApplication.model.TransactionLine;
import com.testApplication.exception.TransactionLineException;
import com.testApplication.service.TransactionLineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-lines")
public class TransactionLineController {

    private final TransactionLineService transactionLineService;    public TransactionLineController(TransactionLineService transactionLineService) {
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
    public ResponseEntity<?> createTransactionLine(
            @RequestBody TransactionLine transactionLine,
            @RequestParam Long transactionId,
            @RequestParam Long accountId) {
        try {
            TransactionLine created = transactionLineService.createTransactionLine(transactionLine, transactionId, accountId);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (TransactionLineException.TransactionNotFoundException | 
                TransactionLineException.AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (TransactionLineException.ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (TransactionLineException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransactionLine(
            @PathVariable Long id,
            @RequestBody TransactionLine transactionLine) {
        try {
            TransactionLine updated = transactionLineService.updateTransactionLine(id, transactionLine);
            return ResponseEntity.ok(updated);
        } catch (TransactionLineException.InvalidTransactionLineException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (TransactionLineException.ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (TransactionLineException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransactionLine(@PathVariable Long id) {
        try {
            transactionLineService.deleteTransactionLine(id);
            return ResponseEntity.noContent().build();
        } catch (TransactionLineException.InvalidTransactionLineException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (TransactionLineException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }

    @GetMapping("/by-transaction/{transactionId}")
    public ResponseEntity<?> getTransactionLinesByTransaction(@PathVariable Long transactionId) {
        try {
            List<TransactionLine> transactionLines = transactionLineService.getTransactionLinesByTransaction(transactionId);
            return ResponseEntity.ok(transactionLines);
        } catch (TransactionLineException.TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (TransactionLineException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<?> getTransactionLinesByAccount(@PathVariable Long accountId) {
        try {
            List<TransactionLine> transactionLines = transactionLineService.getTransactionLinesByAccount(accountId);
            return ResponseEntity.ok(transactionLines);
        } catch (TransactionLineException.AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (TransactionLineException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }
}
