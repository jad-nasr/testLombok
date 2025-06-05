package com.testApplication.mapper;

import com.testApplication.dto.TransactionDTO;
import com.testApplication.model.Transaction;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {
    
    public TransactionDTO toDTO(Transaction entity) {
        return TransactionDTO.builder()
            .id(entity.getId())
            .transactionCode(entity.getTransactionCode())
            .transactionType(entity.getTransactionType())
            .date(entity.getDate())
            .description(entity.getDescription())
            .approvalStatus(entity.getApprovalStatus())
            .amount(entity.getAmount())
            .currency(entity.getCurrency())
            .legalEntityId(entity.getLegalEntity().getId())
            .build();
    }

    public List<TransactionDTO> toDTOList(List<Transaction> entities) {
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}