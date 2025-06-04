package com.testApplication.model.enums;

public enum LegalEntityTypeEnum {
    CORPORATION,
    PARTNERSHIP,
    SOLE_PROPRIETORSHIP,
    NON_PROFIT,
    GOVERNMENT,
    OTHER;

    public String getLegalEntityTypeCode() {
        return this.name();
    }
}