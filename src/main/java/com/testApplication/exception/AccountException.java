package com.testApplication.exception;

import lombok.Getter;

@Getter
public class AccountException extends RuntimeException {
    private final String code;

    protected AccountException(String message, String code) {
        super(message);
        this.code = code;
    }

    public static class InvalidAccountException extends AccountException {
        public InvalidAccountException(String message) {
            super(message, "INVALID_ACCOUNT");
        }
    }

    public static class AccountTypeNotFoundException extends AccountException {
        public AccountTypeNotFoundException(String message) {
            super(message, "ACCOUNT_TYPE_NOT_FOUND");
        }
    }

    public static class LegalEntityNotFoundException extends AccountException {
        public LegalEntityNotFoundException(String message) {
            super(message, "LEGAL_ENTITY_NOT_FOUND");
        }
    }

    public static class ParentAccountNotFoundException extends AccountException {
        public ParentAccountNotFoundException(String message) {
            super(message, "PARENT_ACCOUNT_NOT_FOUND");
        }
    }
}
