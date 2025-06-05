package com.testApplication.controller;

import com.testApplication.dto.AccountTypeDTO;
import com.testApplication.mapper.AccountTypeMapper;
import com.testApplication.service.AccountTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-types")
public class AccountTypeController {

    private final AccountTypeService accountTypeService;
    private final AccountTypeMapper accountTypeMapper;

    public AccountTypeController(AccountTypeService accountTypeService, AccountTypeMapper accountTypeMapper) {
        this.accountTypeService = accountTypeService;
        this.accountTypeMapper = accountTypeMapper;
    }

    @PostMapping
    public ResponseEntity<AccountTypeDTO> createAccountType(@RequestBody AccountTypeDTO accountTypeDTO) {
        return new ResponseEntity<>(
            accountTypeService.createAccountType(accountTypeDTO),
            HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountTypeDTO> getAccountTypeById(@PathVariable Long id) {
        return accountTypeService.getAccountTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<AccountTypeDTO> getAllAccountTypes() {
        return accountTypeService.getAllAccountTypes();
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<AccountTypeDTO> getAccountTypeByCode(@PathVariable String code) {
        return accountTypeService.getAccountTypeByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountTypeDTO> updateAccountType(
            @PathVariable Long id,
            @RequestBody AccountTypeDTO accountTypeDTO) {
        try {
            AccountTypeDTO updatedType = accountTypeService.updateAccountType(id, accountTypeDTO);
            return ResponseEntity.ok(updatedType);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountType(@PathVariable Long id) {
        try {
            accountTypeService.deleteAccountType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
