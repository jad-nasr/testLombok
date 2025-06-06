package com.testApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "accountType", "parentAccount", "legalEntity" })
@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "code", "legal_entity_id" }),
        @UniqueConstraint(columnNames = { "name", "legal_entity_id" })
})
public class Account implements BusinessObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_type_id")
    private AccountType accountType;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount; // Self-referencing FK

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "legal_entity_id", nullable = false)
    private LegalEntity legalEntity;

}