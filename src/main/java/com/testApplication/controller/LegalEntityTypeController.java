package com.testApplication.controller;

import com.testApplication.model.LegalEntityType;
import com.testApplication.repository.LegalEntityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legal-entity-types")
public class LegalEntityTypeController {

    private final LegalEntityTypeRepository legalEntityTypeRepository;

    @Autowired
    public LegalEntityTypeController(LegalEntityTypeRepository legalEntityTypeRepository) {
        this.legalEntityTypeRepository = legalEntityTypeRepository;
    }

    @GetMapping
    public ResponseEntity<List<LegalEntityType>> getAllLegalEntityTypes() {
        return ResponseEntity.ok(legalEntityTypeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalEntityType> getLegalEntityTypeById(@PathVariable Long id) {
        return legalEntityTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<LegalEntityType> getLegalEntityTypeByCode(@PathVariable String code) {
        return legalEntityTypeRepository.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LegalEntityType> createLegalEntityType(@RequestBody LegalEntityType legalEntityType) {
        if (legalEntityTypeRepository.existsByCode(legalEntityType.getCode())) {
            return ResponseEntity.badRequest().build();
        }
        LegalEntityType created = legalEntityTypeRepository.save(legalEntityType);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalEntityType> updateLegalEntityType(
            @PathVariable Long id,
            @RequestBody LegalEntityType legalEntityType) {
        if (!legalEntityTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        legalEntityType.setId(id);
        return ResponseEntity.ok(legalEntityTypeRepository.save(legalEntityType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegalEntityType(@PathVariable Long id) {
        if (!legalEntityTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        legalEntityTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
