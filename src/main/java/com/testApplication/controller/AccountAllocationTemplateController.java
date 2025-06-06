package com.testApplication.controller;

import com.testApplication.dto.AccountAllocationTemplateDTO;
import com.testApplication.exception.AllocationTemplateException;
import com.testApplication.service.AccountAllocationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/allocation-templates")
public class AccountAllocationTemplateController {

    private final AccountAllocationTemplateService templateService;

    @Autowired
    public AccountAllocationTemplateController(AccountAllocationTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<AccountAllocationTemplateDTO> getAllTemplates() {
        return templateService.getAllTemplates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountAllocationTemplateDTO> getTemplateById(@PathVariable Long id) {
        return templateService.getTemplateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<AccountAllocationTemplateDTO> getTemplateByCode(@PathVariable String code) {
        return templateService.getTemplateByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<AccountAllocationTemplateDTO>> getTemplatesByType(
            @RequestParam Boolean isSource) {
        return ResponseEntity.ok(templateService.getTemplatesByType(isSource));
    }

    @GetMapping("/by-legal-entity/{legalEntityId}")
    public ResponseEntity<List<AccountAllocationTemplateDTO>> getTemplatesByLegalEntity(
            @PathVariable Long legalEntityId) {
        return ResponseEntity.ok(templateService.getTemplatesByLegalEntity(legalEntityId));
    }    @PostMapping("/legal-entity/{legalEntityId}")
    public ResponseEntity<?> createTemplate(
            @PathVariable(required = true) Long legalEntityId,
            @RequestBody AccountAllocationTemplateDTO templateDTO) {
        if (legalEntityId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Legal entity ID is required", "code", "INVALID_REQUEST"));
        }
        if (legalEntityId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Legal entity ID must be a positive number", "code", "INVALID_REQUEST"));
        }

        try {
            templateDTO.setLegalEntityId(legalEntityId);
            AccountAllocationTemplateDTO created = templateService.createTemplate(templateDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (AllocationTemplateException.DuplicateTemplateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AllocationTemplateException.LegalEntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AllocationTemplateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }@PutMapping("/{id}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable Long id,
            @RequestBody AccountAllocationTemplateDTO templateDTO) {
        try {
            AccountAllocationTemplateDTO updated = templateService.updateTemplate(id, templateDTO);
            return ResponseEntity.ok(updated);
        } catch (AllocationTemplateException.InvalidTemplateException | AllocationTemplateException.AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AllocationTemplateException.DuplicateTemplateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AllocationTemplateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long id) {
        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (AllocationTemplateException.InvalidTemplateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (AllocationTemplateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }
}
