package com.testApplication.dto;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntityAccessDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long legalEntityId;
    private String legalEntityName;
    private boolean active;
    private Instant grantedAt;
    private String grantedBy;
    private Instant revokedAt;
    private String revokedBy;
}
