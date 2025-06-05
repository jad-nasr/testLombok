package com.testApplication.mapper;

import com.testApplication.dto.LegalEntityTypeDTO;
import com.testApplication.model.LegalEntityType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LegalEntityTypeMapper {
    
    public LegalEntityTypeDTO toDTO(LegalEntityType entity) {
        if (entity == null) {
            return null;
        }

        return LegalEntityTypeDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public List<LegalEntityTypeDTO> toDTOList(List<LegalEntityType> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LegalEntityType toEntity(LegalEntityTypeDTO dto) {
        if (dto == null) {
            return null;
        }

        return LegalEntityType.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
