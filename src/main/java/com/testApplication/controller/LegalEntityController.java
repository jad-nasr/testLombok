package com.testApplication.controller;

import com.testApplication.dto.LegalEntityDTO;
import com.testApplication.service.LegalEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legal-entities")
public class LegalEntityController {

    private final LegalEntityService legalEntityService;

    public LegalEntityController(LegalEntityService legalEntityService) {
        this.legalEntityService = legalEntityService;
    }

    @PostMapping
    public ResponseEntity<LegalEntityDTO> createLegalEntity(
            @RequestBody LegalEntityDTO legalEntityDTO,
            @RequestParam Long legalEntityTypeId) {
        try {
            LegalEntityDTO created = legalEntityService.createLegalEntity(legalEntityTypeId, legalEntityDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<LegalEntityDTO>> getAllLegalEntities() {
        return ResponseEntity.ok(legalEntityService.getAllLegalEntities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalEntityDTO> getLegalEntityById(@PathVariable Long id) {
        return legalEntityService.getLegalEntityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalEntityDTO> updateLegalEntity(
            @PathVariable Long id,
            @RequestBody LegalEntityDTO legalEntityDTO,
            @RequestParam(required = false) Long legalEntityTypeId) {
        try {
            LegalEntityDTO updated = legalEntityService.updateLegalEntity(id, legalEntityDTO, legalEntityTypeId);
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
