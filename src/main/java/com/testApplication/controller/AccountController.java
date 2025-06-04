package com.testApplication.controller;

import com.testApplication.model.Account;
import com.testApplication.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-legal-entity/{legalEntityId}")
    public List<Account> getAccountsByLegalEntity(@PathVariable Long legalEntityId) {
        return accountService.getAccountsByLegalEntity(legalEntityId);
    }

    @GetMapping("/by-parent/{parentAccountId}")
    public List<Account> getAccountsByParentAccount(@PathVariable Long parentAccountId) {
        return accountService.getAccountsByParentAccount(parentAccountId);
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestBody Account account,
            @RequestParam Long legalEntityId,
            @RequestParam Long accountTypeId,
            @RequestParam(required = false) Long parentAccountId) {
        try {
            Account created = accountService.createAccount(legalEntityId, accountTypeId, parentAccountId, account);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable Long id,
            @RequestBody Account account,
            @RequestParam(required = false) Long accountTypeId,
            @RequestParam(required = false) Long parentAccountId) {
        try {
            Account updated = accountService.updateAccount(id, account, accountTypeId, parentAccountId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}