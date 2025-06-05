package com.testApplication.service;

import com.testApplication.dto.AccountTypeDTO;
import com.testApplication.mapper.AccountTypeMapper;
import com.testApplication.model.AccountType;
import com.testApplication.repository.AccountTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountTypeService {

    private final AccountTypeRepository accountTypeRepository;
    private final AccountTypeMapper accountTypeMapper;

    public AccountTypeService(AccountTypeRepository accountTypeRepository, AccountTypeMapper accountTypeMapper) {
        this.accountTypeRepository = accountTypeRepository;
        this.accountTypeMapper = accountTypeMapper;
    }

    @Transactional(readOnly = true)
    public List<AccountTypeDTO> getAllAccountTypes() {
        return accountTypeMapper.toDTOList(accountTypeRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<AccountTypeDTO> getAccountTypeById(Long id) {
        return accountTypeRepository.findById(id)
                .map(accountTypeMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<AccountTypeDTO> getAccountTypeByCode(String code) {
        return accountTypeRepository.findByCode(code)
                .map(accountTypeMapper::toDTO);
    }

    public AccountTypeDTO createAccountType(AccountTypeDTO accountTypeDTO) {
        AccountType accountType = accountTypeMapper.toEntity(accountTypeDTO);
        AccountType savedType = accountTypeRepository.save(accountType);
        return accountTypeMapper.toDTO(savedType);
    }

    public AccountTypeDTO updateAccountType(Long id, AccountTypeDTO accountTypeDTO) {
        AccountType accountType = accountTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account type not found with id: " + id));
        
        accountType.setCode(accountTypeDTO.getCode());
        accountType.setName(accountTypeDTO.getName());
        accountType.setDescription(accountTypeDTO.getDescription());
        
        AccountType updatedType = accountTypeRepository.save(accountType);
        return accountTypeMapper.toDTO(updatedType);
    }

    public void deleteAccountType(Long id) {
        accountTypeRepository.deleteById(id);
    }
}
