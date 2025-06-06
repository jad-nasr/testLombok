package com.testApplication.repository;

import com.testApplication.model.UserEntityAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityAccessRepository extends JpaRepository<UserEntityAccess, Long> {
    List<UserEntityAccess> findByUserId(Long userId);
    List<UserEntityAccess> findByLegalEntityId(Long legalEntityId);
    List<UserEntityAccess> findByUserIdAndActive(Long userId, boolean active);
    Optional<UserEntityAccess> findByUserIdAndLegalEntityId(Long userId, Long legalEntityId);
}
