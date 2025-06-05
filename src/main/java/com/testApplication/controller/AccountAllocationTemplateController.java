package com.testApplication.controller;

import com.testApplication.dto.AccountAllocationTemplateDTO;
import com.testApplication.service.AccountAllocationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    }

    @PostMapping
    public ResponseEntity<AccountAllocationTemplateDTO> createTemplate(
            @RequestBody AccountAllocationTemplateDTO templateDTO) {
        try {
            AccountAllocationTemplateDTO created = templateService.createTemplate(templateDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountAllocationTemplateDTO> updateTemplate(
            @PathVariable Long id,
            @RequestBody AccountAllocationTemplateDTO templateDTO) {
        try {
            AccountAllocationTemplateDTO updated = templateService.updateTemplate(id, templateDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
