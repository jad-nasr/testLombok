package com.testApplication.service;

import com.testApplication.repository.UserRepository;
import com.testApplication.repository.UserEntityAccessRepository;
import com.testApplication.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecurityServiceImpl implements SecurityService {
    
    private final UserRepository userRepository;
    private final UserEntityAccessRepository userEntityAccessRepository;

    @Override
    public boolean hasAccessToLegalEntity(UserDetails principal, Long legalEntityId) {
        // Get the user from the database by username
        return userRepository.findByUsername(principal.getUsername())
            .map(user -> hasAccessToLegalEntity(user.getId(), legalEntityId))
            .orElse(false);
    }

    @Override
    public boolean hasAccessToLegalEntity(Long userId, Long legalEntityId) {
        return userEntityAccessRepository.findByUser_IdAndLegalEntity_Id(userId, legalEntityId)
            .map(access -> access.isActive()) // Check if the access is active
            .orElse(false);
    }
}
