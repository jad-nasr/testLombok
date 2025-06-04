package com.testApplication.model;

import jakarta.persistence.*;
import lombok.*;

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

    public AccountType(String code) {
        this.code = code;
        this.name = code;
    }
}