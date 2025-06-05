package com.testApplication.controller;

import com.testApplication.dto.LegalEntityDTO;
import com.testApplication.mapper.LegalEntityMapper;
import com.testApplication.service.LegalEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legal-entities")
public class LegalEntityController {

    private final LegalEntityService legalEntityService;
    private final LegalEntityMapper legalEntityMapper;

    @Autowired
    public LegalEntityController(LegalEntityService legalEntityService, LegalEntityMapper legalEntityMapper) {
        this.legalEntityService = legalEntityService;
        this.legalEntityMapper = legalEntityMapper;
    }

    @PostMapping
    public ResponseEntity<LegalEntityDTO> createLegalEntity(
            @RequestBody LegalEntityDTO legalEntityDTO,
            @RequestParam Long legalEntityTypeId) {
        return ResponseEntity.ok(legalEntityMapper.toDTO(
            legalEntityService.createLegalEntity(legalEntityTypeId, legalEntityMapper.toEntity(legalEntityDTO))));
    }

    @GetMapping
    public ResponseEntity<List<LegalEntityDTO>> getAllLegalEntities() {
        return ResponseEntity.ok(legalEntityMapper.toDTOList(legalEntityService.getAllLegalEntities()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LegalEntityDTO> getLegalEntityById(@PathVariable Long id) {
        return legalEntityService.getLegalEntityById(id)
                .map(legalEntityMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LegalEntityDTO> updateLegalEntity(
            @PathVariable Long id,
            @RequestBody LegalEntityDTO legalEntityDTO,
            @RequestParam(required = false) Long legalEntityTypeId) {
        return ResponseEntity.ok(legalEntityMapper.toDTO(
            legalEntityService.updateLegalEntity(id, legalEntityMapper.toEntity(legalEntityDTO), legalEntityTypeId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLegalEntity(@PathVariable Long id) {
        legalEntityService.deleteLegalEntity(id);
        return ResponseEntity.noContent().build();
    }
}
