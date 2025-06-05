package com.testApplication.service;

import com.testApplication.dto.TransactionLineDTO;
import com.testApplication.model.*;
import com.testApplication.repository.*;
import com.testApplication.mapper.TransactionLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CsvImportService {
    @Autowired
    private TransactionLineMapper transactionLineMapper;

    @Autowired
    private TransactionLineRepository transactionLineRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LegalEntityRepository legalEntityRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Transactional
    public List<TransactionLineDTO> importTransactionLinesFromCsv(MultipartFile file, Long legalEntityId) {
        List<TransactionLine> transactionLines = new ArrayList<>();
        Map<String, Transaction> transactionCache = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Verify legal entity exists
            LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                    .orElseThrow(() -> new RuntimeException("Legal Entity not found: " + legalEntityId));

            // Skip header line
            String header = reader.readLine();
            validateHeader(header);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                validateData(data);

                // Parse CSV data
                String transactionCode = data[0].trim();
                String accountCode = data[1].trim();
                BigDecimal amount = new BigDecimal(data[2].trim());
                String description = data[3].trim();
                boolean isDebit = Boolean.parseBoolean(data[4].trim());

                // Get or create Transaction
                Transaction transaction = transactionCache.computeIfAbsent(transactionCode, code -> {
                    return transactionRepository.findByTransactionCodeAndLegalEntityId(code, legalEntityId)
                            .orElseGet(() -> createNewTransaction(code, legalEntity));
                });

                // Get or create Account
                Account account = accountRepository.findByCodeAndLegalEntityId(accountCode, legalEntityId)
                        .orElseGet(() -> createNewAccount(accountCode, legalEntity));

                // Create TransactionLine
                TransactionLine transactionLine = TransactionLine.builder()
                        .transaction(transaction)
                        .account(account)
                        .amount(amount)
                        .description(description)
                        .isDebit(isDebit)
                        .build();

                transactionLines.add(transactionLine);
            }

            List<TransactionLine> savedLines = transactionLineRepository.saveAll(transactionLines);
            return transactionLineMapper.toDTOList(savedLines);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import CSV: " + e.getMessage(), e);
        }
    }

    private void validateHeader(String header) {
        String expectedHeader = "transaction_code,account_code,amount,description,is_debit";
        if (!expectedHeader.equalsIgnoreCase(header.trim())) {
            throw new RuntimeException("Invalid CSV header. Expected: " + expectedHeader);
        }
    }

    private void validateData(String[] data) {
        if (data.length != 5) {
            throw new RuntimeException("Invalid CSV line. Expected 5 columns but found " + data.length);
        }
    }

    private Transaction createNewTransaction(String transactionCode, LegalEntity legalEntity) {
        Instant now = Instant.now();
        Transaction transaction = Transaction.builder()
                .transactionCode(transactionCode)
                .transactionType("CSV_IMPORT")
                .date(now)
                .description("Imported from CSV")
                .approvalStatus("PENDING")
                .legalEntity(legalEntity)
                .createdAt(now)
                .createdBy("SYSTEM") // Set created by
                .amount(BigDecimal.ZERO) // Will be updated by transaction lines
                .currency("USD") // Default currency
                .build();
        return transactionRepository.save(transaction);
    }

    private Account createNewAccount(String accountCode, LegalEntity legalEntity) {
        // Default to ASSET type for imported accounts
        AccountType assetType = accountTypeRepository.findByCode("ASSET")
                .orElseThrow(() -> new RuntimeException(
                        "Default ASSET account type not found. Please ensure DataInitializer ran."));

        Instant now = Instant.now();

        Account account = Account.builder()
                .code(accountCode)
                .name("Imported: " + accountCode)
                .description("Auto-created from CSV import")
                .legalEntity(legalEntity)
                .accountType(assetType)
                .active(true)
                .createdAt(now)
                .createdBy("SYSTEM")
                .updatedAt(now)
                .updatedBy("SYSTEM")
                .build();

        try {
            return accountRepository.save(account);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create account: " + accountCode + " - " + e.getMessage(), e);
        }
    }
}
