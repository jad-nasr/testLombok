package com.testApplication.exception;

import lombok.Getter;

@Getter
public class CsvImportException extends RuntimeException {
    private final String code;

    protected CsvImportException(String message, String code) {
        super(message);
        this.code = code;
    }

    public static class InvalidFileException extends CsvImportException {
        public InvalidFileException(String message) {
            super(message, "INVALID_FILE");
        }
    }

    public static class LegalEntityNotFoundException extends CsvImportException {
        public LegalEntityNotFoundException(String message) {
            super(message, "LEGAL_ENTITY_NOT_FOUND");
        }
    }

    public static class InvalidCsvFormatException extends CsvImportException {
        public InvalidCsvFormatException(String message) {
            super(message, "INVALID_CSV_FORMAT");
        }
    }
}
