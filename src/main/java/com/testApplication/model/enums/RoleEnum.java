package com.testApplication.model.enums; // Or com.testApplication.model

// This enum defines the set of possible roles in your application
public enum RoleEnum {
    USER,
    ADMIN,
    EDITOR;
    // Add more roles here as needed

    // Optional: Method to get the conventional Spring Security authority name
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
    }