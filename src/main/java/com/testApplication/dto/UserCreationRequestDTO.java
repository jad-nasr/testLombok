package com.testApplication.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set; // Or List<String>

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequestDTO {
    private String username;
    private String email;
    private String password;
    private Set<String> roles; // Client sends a set of role names, e.g., ["USER", "ADMIN"]
    // Note: storing roles as "ROLE_USER", "ROLE_ADMIN" is convention for Spring Security
}