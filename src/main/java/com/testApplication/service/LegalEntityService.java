package com.testApplication.service;

import com.testApplication.model.LegalEntity;
import com.testApplication.model.LegalEntityType;
import com.testApplication.repository.LegalEntityRepository;
import com.testApplication.repository.LegalEntityTypeRepository;
import com.testApplication.dto.LegalEntityDTO;
import com.testApplication.mapper.LegalEntityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LegalEntityService {

    private final LegalEntityRepository legalEntityRepository;
    private final LegalEntityTypeRepository legalEntityTypeRepository;
    private final LegalEntityMapper legalEntityMapper;

    public LegalEntityService(LegalEntityRepository legalEntityRepository,
                          LegalEntityTypeRepository legalEntityTypeRepository,
                          LegalEntityMapper legalEntityMapper) {
        this.legalEntityRepository = legalEntityRepository;
        this.legalEntityTypeRepository = legalEntityTypeRepository;
        this.legalEntityMapper = legalEntityMapper;
    }

    public LegalEntityDTO createLegalEntity(Long legalEntityTypeId, LegalEntityDTO dto) {
        LegalEntity legalEntity = legalEntityMapper.toEntity(dto);
        
        LegalEntityType legalEntityType = legalEntityTypeRepository.findById(legalEntityTypeId)
                .orElseThrow(() -> new RuntimeException("Legal entity type not found with id: " + legalEntityTypeId));
        legalEntity.setLegalEntityType(legalEntityType);
        legalEntity.setType(legalEntityType.getCode());

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        legalEntity.setCreatedBy(currentUser);
        legalEntity.setCreatedAt(Instant.now());
        legalEntity.setUpdatedBy(currentUser);
        legalEntity.setUpdatedAt(Instant.now());

        LegalEntity saved = legalEntityRepository.save(legalEntity);
        return legalEntityMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<LegalEntityDTO> getAllLegalEntities() {
        return legalEntityMapper.toDTOList(legalEntityRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<LegalEntityDTO> getLegalEntityById(Long id) {
        return legalEntityRepository.findById(id)
                .map(legalEntityMapper::toDTO);
    }

    public LegalEntityDTO updateLegalEntity(Long id, LegalEntityDTO dto, Long legalEntityTypeId) {
        LegalEntity entity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + id));
        
        LegalEntity updated = legalEntityMapper.toEntity(dto);
        entity.setName(updated.getName());
        entity.setRegistrationNumber(updated.getRegistrationNumber());
        entity.setAddress(updated.getAddress());
        entity.setCountry(updated.getCountry());
        entity.setContactPerson(updated.getContactPerson());
        entity.setPhone(updated.getPhone());
        entity.setEmail(updated.getEmail());
        
        if (legalEntityTypeId != null) {
            LegalEntityType type = legalEntityTypeRepository.findById(legalEntityTypeId)
                    .orElseThrow(() -> new RuntimeException("Legal entity type not found with id: " + legalEntityTypeId));
            entity.setLegalEntityType(type);
            entity.setType(type.getCode());
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        entity.setUpdatedBy(currentUser);
        entity.setUpdatedAt(Instant.now());
        
        LegalEntity saved = legalEntityRepository.save(entity);
        return legalEntityMapper.toDTO(saved);
    }

    public void deleteLegalEntity(Long id) {
        legalEntityRepository.deleteById(id);
    }
}