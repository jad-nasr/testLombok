package com.testApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private Long id;
    private String code;
    private String name;
    private Long accountTypeId;
    private String accountTypeName;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long parentAccountId;
    private boolean active;
    private Long legalEntityId;
    private String legalEntityName;
}
