package com.testApplication.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode; // Import
import lombok.ToString;       // Import

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "roles") // Exclude collections from generated equals/hashCode
@ToString(exclude = "roles")          // Exclude collections from generated toString to avoid issues
@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER) // Fetch roles eagerly when a User is loaded
    @JoinTable(
            name = "user_roles", // Name of the join table
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), // FK to User table
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id") // FK to Role table
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}