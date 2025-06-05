package com.testApplication.mapper;

import com.testApplication.dto.AccountCategoryDTO;
import com.testApplication.model.AccountCategory;
import com.testApplication.model.AccountType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountCategoryMapper {
    
    public AccountCategoryDTO toDTO(AccountCategory entity) {
        if (entity == null) {
            return null;
        }        return AccountCategoryDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())                .accountTypeIds(entity.getAccountTypes() != null ? 
                    entity.getAccountTypes().stream()
                        .map(AccountType::getId)
                        .collect(Collectors.toList()) : null)
                .accountTypeCodes(entity.getAccountTypes() != null ?
                    entity.getAccountTypes().stream()
                        .map(AccountType::getCode)
                        .collect(Collectors.toList()) : null)
                .accountTypeNames(entity.getAccountTypes() != null ?
                    entity.getAccountTypes().stream()
                        .map(AccountType::getName)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public List<AccountCategoryDTO> toDTOList(List<AccountCategory> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AccountCategory toEntity(AccountCategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        return AccountCategory.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}