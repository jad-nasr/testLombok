package com.testApplication.model.enums;

import lombok.Getter;

@Getter
public enum AccountCategoryEnum {
    ASSETS("Assets", "Resources owned by the business"),
    LIABILITIES("Liabilities", "Debts and obligations of the business"),
    EQUITY("Equity", "Owner's interest in the business"),
    REVENUE("Revenue", "Income from business activities"),
    EXPENSES("Expenses", "Costs of running the business");

    private final String name;
    private final String description;

    AccountCategoryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
