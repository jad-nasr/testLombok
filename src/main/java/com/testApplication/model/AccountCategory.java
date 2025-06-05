package com.testApplication.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account_categories")
public class AccountCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(length = 500)  
    private String description;
    
    @OneToMany(mappedBy = "accountCategory", fetch = FetchType.LAZY)
    private List<AccountType> accountTypes;
}