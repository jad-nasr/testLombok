package com.testApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountAllocationTemplateDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long legalEntityId;
    private String legalEntityName;
    private Set<AccountAllocationDetails> allocation_details;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountAllocationDetails {
        private Long accountId;
        private String accountCode;
        private Integer allocationOrder;
        private Boolean isSource;
    }
}
