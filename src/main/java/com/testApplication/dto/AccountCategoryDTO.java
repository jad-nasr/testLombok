package com.testApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCategoryDTO {
    private Long id;
    private String code;
    private String name;    private String description;
    private List<Long> accountTypeIds; // Optional - for showing associated account types
    private List<String> accountTypeCodes; // Optional - for showing associated account type codes
    private List<String> accountTypeNames; // Optional - for showing associated account type names
}
