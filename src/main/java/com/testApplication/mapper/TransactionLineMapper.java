package com.testApplication.mapper;

import com.testApplication.dto.TransactionLineDTO;
import com.testApplication.model.TransactionLine;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionLineMapper {
    
    public TransactionLineDTO toDTO(TransactionLine entity) {
        return TransactionLineDTO.builder()
            .id(entity.getId())
            .transactionCode(entity.getTransaction().getTransactionCode())
            .accountCode(entity.getAccount().getCode())
            .accountName(entity.getAccount().getName())
            .amount(entity.getAmount())
            .description(entity.getDescription())
            .isDebit(entity.isDebit())
            .transactionStatus(entity.getTransaction().getApprovalStatus())
            .build();
    }

    public List<TransactionLineDTO> toDTOList(List<TransactionLine> entities) {
        return entities.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}