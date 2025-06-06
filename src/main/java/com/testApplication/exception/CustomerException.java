package com.testApplication.exception;

import lombok.Getter;

@Getter
public class CustomerException extends RuntimeException {
    private final String code;

    protected CustomerException(String message, String code) {
        super(message);
        this.code = code;
    }

    public static class InvalidCustomerException extends CustomerException {
        public InvalidCustomerException(String message) {
            super(message, "INVALID_CUSTOMER");
        }
    }

    public static class LegalEntityNotFoundException extends CustomerException {
        public LegalEntityNotFoundException(String message) {
            super(message, "LEGAL_ENTITY_NOT_FOUND");
        }
    }

    public static class DuplicateCustomerException extends CustomerException {
        public DuplicateCustomerException(String message) {
            super(message, "DUPLICATE_CUSTOMER");
        }
    }
}
