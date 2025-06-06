package com.testApplication.service;

import com.testApplication.dto.UserEntityAccessDTO;
import com.testApplication.model.UserEntityAccess;
import com.testApplication.model.User;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.UserEntityAccessRepository;
import com.testApplication.repository.UserRepository;
import com.testApplication.repository.LegalEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserEntityAccessService {
    private final UserEntityAccessRepository userEntityAccessRepository;
    private final UserRepository userRepository;
    private final LegalEntityRepository legalEntityRepository;

    public UserEntityAccessService(
            UserEntityAccessRepository userEntityAccessRepository,
            UserRepository userRepository,
            LegalEntityRepository legalEntityRepository) {
        this.userEntityAccessRepository = userEntityAccessRepository;
        this.userRepository = userRepository;
        this.legalEntityRepository = legalEntityRepository;
    }

    @Transactional
    public UserEntityAccessDTO grantAccess(Long userId, Long legalEntityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal Entity not found"));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntityAccess existingAccess = userEntityAccessRepository
                .findByUserIdAndLegalEntityId(userId, legalEntityId)
                .orElse(null);

        if (existingAccess != null && existingAccess.isActive()) {
            throw new RuntimeException("User already has active access to this legal entity");
        }

        UserEntityAccess access = UserEntityAccess.builder()
                .user(user)
                .legalEntity(legalEntity)
                .active(true)
                .grantedAt(Instant.now())
                .grantedBy(currentUser)
                .build();

        return toDTO(userEntityAccessRepository.save(access));
    }

    @Transactional
    public UserEntityAccessDTO revokeAccess(Long userId, Long legalEntityId) {
        UserEntityAccess access = userEntityAccessRepository
                .findByUserIdAndLegalEntityId(userId, legalEntityId)
                .orElseThrow(() -> new RuntimeException("Access not found"));

        if (!access.isActive()) {
            throw new RuntimeException("Access is already revoked");
        }

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        access.setActive(false);
        access.setRevokedAt(Instant.now());
        access.setRevokedBy(currentUser);

        return toDTO(userEntityAccessRepository.save(access));
    }

    public List<UserEntityAccessDTO> getUserAccessesByLegalEntity(Long legalEntityId) {
        return userEntityAccessRepository.findByLegalEntityId(legalEntityId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserEntityAccessDTO> getLegalEntityAccessesByUser(Long userId) {
        return userEntityAccessRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserEntityAccessDTO> getAllUserAccessesByLegalEntity(Long legalEntityId) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal Entity not found"));
                
        return legalEntity.getUserAccesses().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private UserEntityAccessDTO toDTO(UserEntityAccess entity) {
        return UserEntityAccessDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .username(entity.getUser().getUsername())
                .legalEntityId(entity.getLegalEntity().getId())
                .legalEntityName(entity.getLegalEntity().getName())
                .active(entity.isActive())
                .grantedAt(entity.getGrantedAt())
                .grantedBy(entity.getGrantedBy())
                .revokedAt(entity.getRevokedAt())
                .revokedBy(entity.getRevokedBy())
                .build();
    }
}
