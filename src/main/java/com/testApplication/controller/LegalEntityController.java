package com.testApplication.controller;

import com.testApplication.model.LegalEntity;
import com.testApplication.service.LegalEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legal-entities")
public class LegalEntityController {

    private final LegalEntityService legalEntityService;

    @Autowired
    public LegalEntityController(LegalEntityService legalEntityService) {
        this.legalEntityService = legalEntityService;
    }

    @GetMapping
    public ResponseEntity<List<LegalEntity>> getAllLegalEntities() {
        return ResponseEntity.ok(legalEntityService.getAllLegalEntities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalEntity> getLegalEntityById(@PathVariable Long id) {
        return legalEntityService.getLegalEntityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LegalEntity> createLegalEntity(
            @RequestBody LegalEntity legalEntity,
            @RequestParam Long legalEntityTypeId) {
        LegalEntity created = legalEntityService.createLegalEntity(legalEntityTypeId, legalEntity);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalEntity> updateLegalEntity(
            @PathVariable Long id,
            @RequestBody LegalEntity legalEntity,
            @RequestParam Long legalEntityTypeId) {
        try {
            LegalEntity updated = legalEntityService.updateLegalEntity(id, legalEntity, legalEntityTypeId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegalEntity(@PathVariable Long id) {
        try {
            legalEntityService.deleteLegalEntity(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
