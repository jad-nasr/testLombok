package com.testApplication.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String transactionCode;
    private String transactionType;
    private Instant date;
    private String description;
    private String approvalStatus;
    private BigDecimal amount;
    private String currency;
    private Long legalEntityId;
    private Long customerId;  // Add if missing
}