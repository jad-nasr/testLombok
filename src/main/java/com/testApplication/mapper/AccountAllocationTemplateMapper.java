package com.testApplication.mapper;

import com.testApplication.dto.AccountAllocationTemplateDTO;
import com.testApplication.dto.AccountAllocationTemplateDTO.AccountAllocationDetails;
import com.testApplication.model.AccountAllocationTemplate;
import com.testApplication.model.AccountAllocationTemplateAccount;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AccountAllocationTemplateMapper {
      public AccountAllocationTemplateDTO toDTO(AccountAllocationTemplate entity) {
        if (entity == null) {
            return null;
        }

        Set<AccountAllocationDetails> accounts = entity.getTemplateAccounts().stream()
                .map(this::toAccountAllocationDetails)
                .collect(Collectors.toSet());

        return AccountAllocationTemplateDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .legalEntityId(entity.getLegalEntity() != null ? entity.getLegalEntity().getId() : null)
                .legalEntityName(entity.getLegalEntity() != null ? entity.getLegalEntity().getName() : null)
                .allocation_details(accounts)
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private AccountAllocationDetails toAccountAllocationDetails(AccountAllocationTemplateAccount templateAccount) {
        return AccountAllocationDetails.builder()
                .accountId(templateAccount.getAccount().getId())
                .accountCode(templateAccount.getAccount().getCode())
                .allocationOrder(templateAccount.getAllocationOrder())
                .isSource(templateAccount.getIsSource())
                .build();
    }

    public List<AccountAllocationTemplateDTO> toDTOList(List<AccountAllocationTemplate> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
