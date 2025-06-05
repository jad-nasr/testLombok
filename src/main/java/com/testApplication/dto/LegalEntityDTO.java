package com.testApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalEntityDTO {
    private Long id;
    private String name;
    private Long legalEntityTypeId;
    private String legalEntityTypeName;
    private String registrationNumber;
    private String address;
    private String country;
    private String contactPerson;
    private String phone;
    private String email;
    private String type;
}
