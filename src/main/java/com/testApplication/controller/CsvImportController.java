package com.testApplication.controller;

import com.testApplication.dto.TransactionLineDTO;
import com.testApplication.service.CsvImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/import")
public class CsvImportController {

    private final CsvImportService csvImportService;

    @Autowired
    public CsvImportController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }    @PostMapping("/transaction-lines")
    public ResponseEntity<List<TransactionLineDTO>> importTransactionLines(
            @RequestParam("file") MultipartFile file,
            @RequestParam("legalEntityId") Long legalEntityId) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Validate content type
            if (!file.getContentType().equals("text/csv")) {
                return ResponseEntity.badRequest().build();
            }

            List<TransactionLineDTO> imported = csvImportService.importTransactionLinesFromCsv(file, legalEntityId);
            return ResponseEntity.ok(imported);
        } catch (RuntimeException e) {
            // Return 400 Bad Request for validation errors and other client-side issues
            return ResponseEntity.badRequest().build();
        }
    }
}