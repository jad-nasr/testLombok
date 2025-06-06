package com.testApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account_types")
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g., "ASSET", "LIABILITY", etc.

    @Column(nullable = false, length = 255)
    private String name; // e.g., "Asset", "Liability", etc.

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_category_id")
    private AccountCategory accountCategory;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    public AccountType(String code) {
        this.code = code;
        // Convert UPPER_CASE to Title Case for name
        this.name = code.charAt(0) + code.substring(1).toLowerCase();
    }
}