package com.testApplication.mapper;

import com.testApplication.dto.AccountTypeDTO;
import com.testApplication.model.AccountType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountTypeMapper {
    
    public AccountTypeDTO toDTO(AccountType entity) {
        if (entity == null) {
            return null;
        }

        return AccountTypeDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public List<AccountTypeDTO> toDTOList(List<AccountType> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AccountType toEntity(AccountTypeDTO dto) {
        if (dto == null) {
            return null;
        }

        return AccountType.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
