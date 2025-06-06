package com.testApplication.exception;

import lombok.Getter;

@Getter
public class TransactionLineException extends RuntimeException {
    private final String code;

    protected TransactionLineException(String message, String code) {
        super(message);
        this.code = code;
    }

    public static class InvalidTransactionLineException extends TransactionLineException {
        public InvalidTransactionLineException(String message) {
            super(message, "INVALID_TRANSACTION_LINE");
        }
    }

    public static class TransactionNotFoundException extends TransactionLineException {
        public TransactionNotFoundException(String message) {
            super(message, "TRANSACTION_NOT_FOUND");
        }
    }

    public static class AccountNotFoundException extends TransactionLineException {
        public AccountNotFoundException(String message) {
            super(message, "ACCOUNT_NOT_FOUND");
        }
    }

    public static class ValidationException extends TransactionLineException {
        public ValidationException(String message) {
            super(message, "VALIDATION_ERROR");
        }
    }
}
