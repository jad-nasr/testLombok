package com.testApplication.repository;

import com.testApplication.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {    List<Customer> findByLegalEntity_Id(Long legalEntityId);
    List<Customer> findByType(String type);
}