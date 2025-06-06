package com.testApplication.exception;

public class AllocationTemplateException extends RuntimeException {
    private final String code;

    public AllocationTemplateException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static class DuplicateTemplateException extends AllocationTemplateException {
        public DuplicateTemplateException(String code, Long legalEntityId) {
            super("Template with code " + code + " already exists for legal entity ID: " + legalEntityId, "DUPLICATE_TEMPLATE");
        }
    }

    public static class LegalEntityNotFoundException extends AllocationTemplateException {
        public LegalEntityNotFoundException(Long id) {
            super("Legal entity not found with ID: " + id, "LEGAL_ENTITY_NOT_FOUND");
        }
    }

    public static class AccountNotFoundException extends AllocationTemplateException {
        public AccountNotFoundException(String accountCode, Long legalEntityId) {
            super("Account not found with code: " + accountCode + " for legal entity ID: " + legalEntityId, "ACCOUNT_NOT_FOUND");
        }
    }

    public static class InvalidTemplateException extends AllocationTemplateException {
        public InvalidTemplateException(String message) {
            super(message, "INVALID_TEMPLATE");
        }
    }
}
