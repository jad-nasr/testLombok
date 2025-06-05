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

        return AccountTypeDTO.builder()                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .accountCategoryId(entity.getAccountCategory() != null ? entity.getAccountCategory().getId() : null)
                .accountCategoryCode(entity.getAccountCategory() != null ? entity.getAccountCategory().getCode() : null)
                .accountCategoryName(entity.getAccountCategory() != null ? entity.getAccountCategory().getName() : null)
                .build();
    }

    public List<AccountTypeDTO> toDTOList(List<AccountType> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }    public AccountType toEntity(AccountTypeDTO dto) {
        if (dto == null) {
            return null;
        }

        AccountType entity = new AccountType();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        // Note: AccountCategory will be set by the service
        return entity;
    }
}
