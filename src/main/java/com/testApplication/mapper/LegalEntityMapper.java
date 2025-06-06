package com.testApplication.mapper;

import com.testApplication.dto.LegalEntityDTO;
import com.testApplication.dto.UserEntityAccessDTO;
import com.testApplication.model.LegalEntity;
import com.testApplication.model.LegalEntityType;
import com.testApplication.model.UserEntityAccess;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LegalEntityMapper {
    
    public LegalEntityDTO toDTO(LegalEntity entity) {
        if (entity == null) {
            return null;
        }

        List<UserEntityAccessDTO> userDTOs = entity.getUserAccesses().stream()
            .map(this::toUserAccessDTO)
            .collect(Collectors.toList());

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
                .users(userDTOs)
                .build();
    }

    private UserEntityAccessDTO toUserAccessDTO(UserEntityAccess entity) {
        return UserEntityAccessDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .username(entity.getUser().getUsername())
                .legalEntityId(entity.getLegalEntity().getId())
                .legalEntityName(entity.getLegalEntity().getName())
                .active(entity.isActive())
                .grantedAt(entity.getGrantedAt())
                .grantedBy(entity.getGrantedBy())
                .revokedAt(entity.getRevokedAt())
                .revokedBy(entity.getRevokedBy())
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
