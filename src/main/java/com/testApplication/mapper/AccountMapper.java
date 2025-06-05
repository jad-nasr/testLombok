package com.testApplication.mapper;

import com.testApplication.dto.AccountDTO;
import com.testApplication.model.Account;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {
    
    public AccountDTO toDTO(Account entity) {
        if (entity == null) {
            return null;
        }

        return AccountDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .accountTypeId(entity.getAccountType() != null ? entity.getAccountType().getId() : null)
                .accountTypeName(entity.getAccountType() != null ? entity.getAccountType().getName() : null)
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .parentAccountId(entity.getParentAccount() != null ? entity.getParentAccount().getId() : null)
                .active(entity.isActive())
                .legalEntityId(entity.getLegalEntity() != null ? entity.getLegalEntity().getId() : null)
                .legalEntityName(entity.getLegalEntity() != null ? entity.getLegalEntity().getName() : null)
                .build();
    }

    public List<AccountDTO> toDTOList(List<Account> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Account toEntity(AccountDTO dto) {
        if (dto == null) {
            return null;
        }

        Account entity = new Account();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setActive(dto.isActive());
        return entity;
    }
}
