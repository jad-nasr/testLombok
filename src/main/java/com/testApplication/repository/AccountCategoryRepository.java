package com.testApplication.repository;

import com.testApplication.model.AccountCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AccountCategoryRepository extends JpaRepository<AccountCategory, Long> {
    Optional<AccountCategory> findByCode(String code);
    List<AccountCategory> findAllByOrderByCodeAsc();
    boolean existsByCode(String code);
}