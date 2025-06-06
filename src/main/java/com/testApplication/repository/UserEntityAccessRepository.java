package com.testApplication.repository;

import com.testApplication.model.UserEntityAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityAccessRepository extends JpaRepository<UserEntityAccess, Long> {    List<UserEntityAccess> findByUser_Id(Long userId);
    List<UserEntityAccess> findByLegalEntity_Id(Long legalEntityId);
    List<UserEntityAccess> findByUser_IdAndActive(Long userId, boolean active);
    Optional<UserEntityAccess> findByUser_IdAndLegalEntity_Id(Long userId, Long legalEntityId);
}
