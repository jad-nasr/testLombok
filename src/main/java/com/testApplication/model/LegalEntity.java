package com.testApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "legalEntityType")
@ToString(exclude = "legalEntityType")
@Entity
@Table(name = "legal_entities")
public class LegalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "legal_entity_type_id", nullable = false)
    private LegalEntityType legalEntityType;

    @Column(length = 100)
    private String registrationNumber;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private String type;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}