package com.testApplication.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityService {
    boolean hasAccessToLegalEntity(UserDetails principal, Long legalEntityId);
    boolean hasAccessToLegalEntity(Long userId, Long legalEntityId);
}
