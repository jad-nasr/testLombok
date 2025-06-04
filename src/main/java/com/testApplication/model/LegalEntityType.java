package com.testApplication.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "legal_entity_types")
public class LegalEntityType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g., "CORPORATION", "PARTNERSHIP", etc.

    @Column(nullable = false, length = 255)
    private String name; // e.g., "Corporation", "Partnership", etc.

    @Column(length = 500)
    private String description;

    public LegalEntityType(String code) {
        this.code = code;
        this.name = code;
    }
}