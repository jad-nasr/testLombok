package com.testApplication.dto;

import java.math.BigDecimal;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class TransactionLineDTO {
    private Long id;
    private String transactionCode;
    private String accountCode;
    private String accountName;
    private BigDecimal amount;
    private String description;
    private boolean isDebit;
    private String transactionStatus;
}