package com.testApplication.repository;

import com.testApplication.model.LegalEntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LegalEntityTypeRepository extends JpaRepository<LegalEntityType, Long> {
    Optional<LegalEntityType> findByCode(String code);
    boolean existsByCode(String code);
}