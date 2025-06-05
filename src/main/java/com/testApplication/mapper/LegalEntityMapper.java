package com.testApplication.mapper;

import com.testApplication.dto.LegalEntityDTO;
import com.testApplication.model.LegalEntity;
import com.testApplication.model.LegalEntityType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LegalEntityMapper {
    
    public LegalEntityDTO toDTO(LegalEntity entity) {
        if (entity == null) {
            return null;
        }

        return LegalEntityDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .legalEntityTypeId(entity.getLegalEntityType() != null ? entity.getLegalEntityType().getId() : null)
                .legalEntityTypeName(entity.getLegalEntityType() != null ? entity.getLegalEntityType().getName() : null)
                .registrationNumber(entity.getRegistrationNumber())
                .address(entity.getAddress())
                .country(entity.getCountry())
                .contactPerson(entity.getContactPerson())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .type(entity.getType())
                .build();
    }

    public List<LegalEntityDTO> toDTOList(List<LegalEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LegalEntity toEntity(LegalEntityDTO dto) {
        if (dto == null) {
            return null;
        }

        LegalEntity entity = new LegalEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setRegistrationNumber(dto.getRegistrationNumber());
        entity.setAddress(dto.getAddress());
        entity.setCountry(dto.getCountry());
        entity.setContactPerson(dto.getContactPerson());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setType(dto.getType());
        return entity;
    }
}
