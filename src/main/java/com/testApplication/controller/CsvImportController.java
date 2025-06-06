package com.testApplication.controller;

import com.testApplication.dto.TransactionLineDTO;
import com.testApplication.exception.CsvImportException;
import com.testApplication.service.CsvImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/import")
public class CsvImportController {

    private final CsvImportService csvImportService;

    public CsvImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping("/transaction-lines")
    public ResponseEntity<?> importTransactionLines(
            @RequestParam("file") MultipartFile file,
            @RequestParam("legalEntityId") Long legalEntityId) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new CsvImportException.InvalidFileException("File is empty");
            }
            
            // Validate content type
            if (!file.getContentType().equals("text/csv")) {
                throw new CsvImportException.InvalidFileException("File must be a CSV file");
            }

            List<TransactionLineDTO> imported = csvImportService.importTransactionLinesFromCsv(file, legalEntityId);
            return ResponseEntity.ok(imported);
        } catch (CsvImportException.LegalEntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (CsvImportException.InvalidFileException | CsvImportException.InvalidCsvFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        } catch (CsvImportException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "code", e.getCode()));
        }
    }
}