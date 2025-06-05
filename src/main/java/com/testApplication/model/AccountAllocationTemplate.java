package com.testApplication.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "templateAccounts") // Exclude collections from equals/hashCode
@ToString(exclude = "templateAccounts") // Exclude collections from toString
@Entity
@Table(name = "account_allocation_templates",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code", "legal_entity_id"})
    }
)
public class AccountAllocationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_entity_id", nullable = false)
    private LegalEntity legalEntity;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AccountAllocationTemplateAccount> templateAccounts = new HashSet<>();

    @Column(length = 100)
    private String createdBy;

    @Column(length = 100)
    private String updatedBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
