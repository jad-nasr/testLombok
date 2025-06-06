package com.testApplication.service;

import com.testApplication.dto.CustomerDTO;
import com.testApplication.mapper.CustomerMapper;
import com.testApplication.model.Customer;
import com.testApplication.model.LegalEntity;
import com.testApplication.repository.CustomerRepository;
import com.testApplication.repository.LegalEntityRepository;
import com.testApplication.security.RequiresLegalEntityAccess;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final LegalEntityRepository legalEntityRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository,
                         LegalEntityRepository legalEntityRepository,
                         CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.legalEntityRepository = legalEntityRepository;
        this.customerMapper = customerMapper;
    }

    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    public CustomerDTO createCustomer(CustomerDTO customerDTO, Long legalEntityId) {
        LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
        
        Customer customer = customerMapper.toEntity(customerDTO);
        customer.setLegalEntity(legalEntity);
        
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        customer.setCreatedBy(currentUser);
        customer.setCreatedAt(Instant.now());
        customer.setUpdatedBy(currentUser);
        customer.setUpdatedAt(Instant.now());
        
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    @Transactional(readOnly = true)
    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerMapper.toDTOList(customerRepository.findAll());
    }    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersByLegalEntity(Long legalEntityId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new RuntimeException("Legal entity not found with id: " + legalEntityId);
        }
        return customerMapper.toDTOList(customerRepository.findByLegalEntity_Id(legalEntityId));
    }

    @RequiresLegalEntityAccess(legalEntityIdParam = "legalEntityId")
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO, Long legalEntityId) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (legalEntityId != null) {
            LegalEntity legalEntity = legalEntityRepository.findById(legalEntityId)
                    .orElseThrow(() -> new RuntimeException("Legal entity not found with id: " + legalEntityId));
            customer.setLegalEntity(legalEntity);
        }

        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setAddress(customerDTO.getAddress());
        customer.setType(customerDTO.getType());
        
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        customer.setUpdatedBy(currentUser);
        customer.setUpdatedAt(Instant.now());
        
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}