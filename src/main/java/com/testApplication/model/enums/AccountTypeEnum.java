package com.testApplication.model.enums;

import lombok.Getter;

@Getter
public enum AccountTypeEnum {
    CURRENT_ASSET("Current Asset", "Short-term assets", "ASSETS"),
    FIXED_ASSET("Fixed Asset", "Long-term assets", "ASSETS"),
    CURRENT_LIABILITY("Current Liability", "Short-term liabilities", "LIABILITIES"),
    LONG_TERM_LIABILITY("Long Term Liability", "Long-term liabilities", "LIABILITIES"),
    OWNER_EQUITY("Owner's Equity", "Owner's investment", "EQUITY"),
    RETAINED_EARNINGS("Retained Earnings", "Accumulated profits", "EQUITY"),
    OPERATING_REVENUE("Operating Revenue", "Income from main business", "REVENUE"),
    OTHER_REVENUE("Other Revenue", "Income from other sources", "REVENUE"),
    OPERATING_EXPENSE("Operating Expense", "Main business costs", "EXPENSES"),
    OTHER_EXPENSE("Other Expense", "Other costs", "EXPENSES");

    private final String name;
    private final String description;
    private final String categoryCode;

    AccountTypeEnum(String name, String description, String categoryCode) {
        this.name = name;
        this.description = description;
        this.categoryCode = categoryCode;
    }
}