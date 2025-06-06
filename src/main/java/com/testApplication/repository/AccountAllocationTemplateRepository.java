package com.testApplication.repository;

import com.testApplication.model.AccountAllocationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AccountAllocationTemplateRepository extends JpaRepository<AccountAllocationTemplate, Long> {    Optional<AccountAllocationTemplate> findByCodeAndLegalEntity_Id(String code, Long legalEntityId);
    boolean existsByCodeAndLegalEntity_Id(String code, Long legalEntityId);
    List<AccountAllocationTemplate> findByTemplateAccounts_IsSource(Boolean isSource);
    List<AccountAllocationTemplate> findByLegalEntity_Id(Long legalEntityId);
}
