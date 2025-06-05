package com.testApplication.controller;

import com.testApplication.dto.AccountDTO;
import com.testApplication.mapper.AccountMapper;
import com.testApplication.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @GetMapping
    public List<AccountDTO> getAllAccounts() {
        return accountMapper.toDTOList(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(accountMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-legal-entity/{legalEntityId}")
    public List<AccountDTO> getAccountsByLegalEntity(@PathVariable Long legalEntityId) {
        return accountMapper.toDTOList(accountService.getAccountsByLegalEntity(legalEntityId));
    }

    @GetMapping("/by-parent/{parentAccountId}")
    public List<AccountDTO> getAccountsByParentAccount(@PathVariable Long parentAccountId) {
        return accountMapper.toDTOList(accountService.getAccountsByParentAccount(parentAccountId));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(
            @RequestBody AccountDTO accountDTO,
            @RequestParam Long legalEntityId,
            @RequestParam Long accountTypeId,
            @RequestParam(required = false) Long parentAccountId) {
        try {
            return new ResponseEntity<>(
                accountMapper.toDTO(
                    accountService.createAccount(legalEntityId, accountTypeId, parentAccountId, accountMapper.toEntity(accountDTO))
                ),
                HttpStatus.CREATED
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id,
            @RequestBody AccountDTO accountDTO,
            @RequestParam(required = false) Long accountTypeId,
            @RequestParam(required = false) Long parentAccountId) {
        try {
            return ResponseEntity.ok(
                accountMapper.toDTO(
                    accountService.updateAccount(id, accountMapper.toEntity(accountDTO), accountTypeId, parentAccountId)
                )
            );
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