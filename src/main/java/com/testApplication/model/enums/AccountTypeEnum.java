package com.testApplication.model.enums;

public enum AccountTypeEnum {
    ASSET,
    LIABILITY,
    EQUITY,
    REVENUE,
    EXPENSE;

    public String getAccountTypeCode() {
        return this.name();
    }
}