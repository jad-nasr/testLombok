package com.testApplication.controller;

import com.testApplication.dto.AccountDTO;
import com.testApplication.exception.AccountException;
import com.testApplication.mapper.AccountMapper;
import com.testApplication.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }    @GetMapping
    public List<AccountDTO> getAllAccounts(@RequestParam Long legalEntityId) {
        return accountService.getAllAccounts(legalEntityId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id, @RequestParam Long legalEntityId) {
        return accountService.getAccountById(legalEntityId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-legal-entity/{legalEntityId}")
    public List<AccountDTO> getAccountsByLegalEntity(@PathVariable Long legalEntityId) {
        return accountService.getAccountsByLegalEntity(legalEntityId);
    }

    @GetMapping("/by-parent/{parentAccountId}")
    public List<AccountDTO> getAccountsByParentAccount(
            @PathVariable Long parentAccountId,
            @RequestParam Long legalEntityId) {
        return accountService.getAccountsByParentAccount(legalEntityId, parentAccountId);
    }    @PostMapping
    public ResponseEntity<?> createAccount(
            @RequestBody AccountDTO accountDTO,
            @RequestParam Long legalEntityId,
            @RequestParam Long accountTypeId,
            @RequestParam(required = false) Long parentAccountId) {
        try {
            AccountDTO created = accountService.createAccount(legalEntityId, accountTypeId, parentAccountId, accountDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (AccountException.LegalEntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AccountException.AccountTypeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AccountException.ParentAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AccountException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(
            @PathVariable Long id,
            @RequestBody AccountDTO accountDTO,
            @RequestParam Long legalEntityId,
            @RequestParam(required = false) Long accountTypeId,
            @RequestParam(required = false) Long parentAccountId) {
        try {
            AccountDTO updated = accountService.updateAccount(legalEntityId, id, accountDTO, accountTypeId, parentAccountId);
            return ResponseEntity.ok(updated);
        } catch (AccountException.InvalidAccountException | AccountException.AccountTypeNotFoundException | 
                AccountException.ParentAccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AccountException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(
            @PathVariable Long id,
            @RequestParam Long legalEntityId) {
        try {
            accountService.deleteAccount(legalEntityId, id);
            return ResponseEntity.noContent().build();
        } catch (AccountException.InvalidAccountException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AccountException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }
}