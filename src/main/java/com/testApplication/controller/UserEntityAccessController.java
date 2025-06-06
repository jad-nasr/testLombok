package com.testApplication.controller;

import com.testApplication.dto.UserEntityAccessDTO;
import com.testApplication.service.UserEntityAccessService;
import com.testApplication.service.LegalEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing user access to legal entities.
 * Provides endpoints for granting, revoking, and querying user access rights.
 */
@RestController
@RequestMapping("/api/user-entity-access")
public class UserEntityAccessController {
    private final UserEntityAccessService userEntityAccessService;
    private final LegalEntityService legalEntityService;

    public UserEntityAccessController(
            UserEntityAccessService userEntityAccessService,
            LegalEntityService legalEntityService) {
        this.userEntityAccessService = userEntityAccessService;
        this.legalEntityService = legalEntityService;
    }

    /**
     * Grant access for a user to a legal entity.
     * @param legalEntityId ID of the legal entity
     * @param userId ID of the user
     * @return The created user access details
     */
    @PostMapping("/legal-entity/{legalEntityId}/user/{userId}")
    public ResponseEntity<?> grantAccess(
            @PathVariable Long legalEntityId,
            @PathVariable Long userId) {
        try {
            UserEntityAccessDTO granted = userEntityAccessService.grantAccess(userId, legalEntityId);
            return new ResponseEntity<>(granted, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Revoke access for a user to a legal entity.
     * @param legalEntityId ID of the legal entity
     * @param userId ID of the user
     * @return The updated user access details
     */
    @DeleteMapping("/legal-entity/{legalEntityId}/user/{userId}")
    public ResponseEntity<?> revokeAccess(
            @PathVariable Long legalEntityId,
            @PathVariable Long userId) {
        try {
            UserEntityAccessDTO revoked = userEntityAccessService.revokeAccess(userId, legalEntityId);
            return ResponseEntity.ok(revoked);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all active user accesses for a legal entity.
     * @param legalEntityId ID of the legal entity
     * @param includeInactive Whether to include inactive user accesses
     * @return List of user access details
     */    @GetMapping("/legal-entity/{legalEntityId}/users")
    public ResponseEntity<List<UserEntityAccessDTO>> getUsersByLegalEntity(
            @PathVariable Long legalEntityId,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        return legalEntityService.getLegalEntityById(legalEntityId)
                .map(dto -> {
                    List<UserEntityAccessDTO> users = dto.getUsers();
                    if (!includeInactive) {
                        users = users.stream()
                                .filter(UserEntityAccessDTO::isActive)
                                .toList();
                    }
                    return ResponseEntity.ok(users);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all user accesses for a legal entity, including historical data.
     * @param legalEntityId ID of the legal entity
     * @return List of all user access details
     */
    @GetMapping("/legal-entity/{legalEntityId}/users/all")
    public ResponseEntity<List<UserEntityAccessDTO>> getAllUsersByLegalEntity(
            @PathVariable Long legalEntityId) {
        return legalEntityService.getLegalEntityById(legalEntityId)
                .map(dto -> ResponseEntity.ok(dto.getUsers()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all legal entity accesses for a user.
     * @param userId ID of the user
     * @param includeInactive Whether to include inactive accesses
     * @return List of legal entity access details
     */
    @GetMapping("/user/{userId}/legal-entities")
    public ResponseEntity<List<UserEntityAccessDTO>> getLegalEntitiesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        List<UserEntityAccessDTO> entities = userEntityAccessService.getLegalEntityAccessesByUser(userId);
        if (!includeInactive) {
            entities = entities.stream()
                    .filter(UserEntityAccessDTO::isActive)
                    .toList();
        }
        return ResponseEntity.ok(entities);
    }
}
