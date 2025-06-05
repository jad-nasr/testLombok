package com.testApplication.controller;

import com.testApplication.dto.CustomerDTO;
import com.testApplication.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @RequestBody CustomerDTO customerDTO,
            @RequestParam Long legalEntityId) {
        return new ResponseEntity<>(
            customerService.createCustomer(customerDTO, legalEntityId),
            HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/by-legal-entity/{legalEntityId}")
    public List<CustomerDTO> getCustomersByLegalEntity(@PathVariable Long legalEntityId) {
        return customerService.getCustomersByLegalEntity(legalEntityId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO,
            @RequestParam(required = false) Long legalEntityId) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO, legalEntityId);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}