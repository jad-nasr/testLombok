package com.testApplication.mapper;

import com.testApplication.dto.CustomerDTO;
import com.testApplication.model.Customer;
import com.testApplication.model.LegalEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    
    public CustomerDTO toDTO(Customer entity) {
        if (entity == null) {
            return null;
        }

        return CustomerDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .legalEntityId(entity.getLegalEntity() != null ? entity.getLegalEntity().getId() : null)
                .legalEntityName(entity.getLegalEntity() != null ? entity.getLegalEntity().getName() : null)
                .build();
    }

    public List<CustomerDTO> toDTOList(List<Customer> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Customer toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setType(dto.getType());
        customer.setCreatedAt(dto.getCreatedAt());
        customer.setUpdatedAt(dto.getUpdatedAt());
        
        if (dto.getLegalEntityId() != null) {
            LegalEntity legalEntity = new LegalEntity();
            legalEntity.setId(dto.getLegalEntityId());
            legalEntity.setName(dto.getLegalEntityName());
            customer.setLegalEntity(legalEntity);
        }
        
        return customer;
    }
}
