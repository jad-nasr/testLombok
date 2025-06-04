package com.testApplication.service;

import com.testApplication.model.LegalEntity;
import com.testApplication.model.LegalEntityType;
import com.testApplication.repository.LegalEntityRepository;
import com.testApplication.repository.LegalEntityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LegalEntityService {

    private final LegalEntityRepository legalEntityRepository;
    private final LegalEntityTypeRepository legalEntityTypeRepository;

    @Autowired
    public LegalEntityService(LegalEntityRepository legalEntityRepository,
                              LegalEntityTypeRepository legalEntityTypeRepository) {
        this.legalEntityRepository = legalEntityRepository;
        this.legalEntityTypeRepository = legalEntityTypeRepository;
    }

    public LegalEntity createLegalEntity(Long legalEntityTypeId, LegalEntity legalEntity) {
        LegalEntityType type = legalEntityTypeRepository.findById(legalEntityTypeId)
                .orElseThrow(() -> new RuntimeException("Legal entity type not found with id: " + legalEntityTypeId));
        legalEntity.setLegalEntityType(type);
        return legalEntityRepository.save(legalEntity);
    }

    public List<LegalEntity> getAllLegalEntities() {
        return legalEntityRepository.findAll();
    }

    public Optional<LegalEntity> getLegalEntityById(Long id) {
        return legalEntityRepository.findById(id);
    }

    public LegalEntity updateLegalEntity(Long id, LegalEntity updated, Long legalEntityTypeId) {
        LegalEntity entity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + id));
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
        }
        return legalEntityRepository.save(entity);
    }

    public void deleteLegalEntity(Long id) {
        legalEntityRepository.deleteById(id);
    }
}