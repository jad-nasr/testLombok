package com.testApplication.service;

import com.testApplication.dto.UserCreationRequestDTO;
import com.testApplication.model.Role;    // Your Role JPA Entity
import com.testApplication.model.User;    // Your User JPA Entity
import com.testApplication.model.enums.RoleEnum; // Your RoleEnum
import com.testApplication.repository.RoleRepository;
import com.testApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // loadUserByUsername remains the same
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(),
                true, true, true, true, authorities);
    }


    /**
     * Converts a set of role strings (e.g., "USER", "ADMIN") from a DTO
     * to a Set of managed Role entities.
     * Throws an exception if a role name is invalid or not found in the database.
     *
     * @param roleNamesFromDto Set of strings like "USER", "ADMIN" from the DTO
     * @return Set of managed Role entities
     * @throws IllegalArgumentException if a role name from DTO is not defined in RoleEnum
     * @throws RuntimeException if a role defined in RoleEnum is not found in the database (configuration error)
     */
    private Set<Role> getRolesFromNames(Set<String> roleNamesFromDto) {
        Set<Role> userRoles = new HashSet<>();

        // Handle case where no roles are specified in DTO - assign a default or throw error
        if (roleNamesFromDto == null || roleNamesFromDto.isEmpty()) {
            // Option 1: Assign a default role
            System.out.println("No roles provided in DTO, assigning default role: " + RoleEnum.USER.getAuthority());
            Role defaultRole = roleRepository.findByName(RoleEnum.USER.getAuthority())
                    .orElseThrow(() -> new RuntimeException("Configuration error: Default role " +
                            RoleEnum.USER.getAuthority() + " not found in DB. Ensure DataInitializer ran."));
            userRoles.add(defaultRole);
            return userRoles;
            // Option 2: Or, throw an exception if roles are mandatory
            // throw new IllegalArgumentException("Roles must be specified for the user.");
        }

        // Process roles specified in DTO
        for (String roleNameFromClient : roleNamesFromDto) {
            String trimmedRoleName = roleNameFromClient.trim().toUpperCase();
            RoleEnum roleEnumConstant;
            try {
                // Step 1: Validate against RoleEnum
                roleEnumConstant = RoleEnum.valueOf(trimmedRoleName);
            } catch (IllegalArgumentException e) {
                // Role name from DTO is not a valid constant in RoleEnum
                throw new IllegalArgumentException("Invalid role specified: '" + roleNameFromClient +
                        "'. Valid roles are: " +
                        Arrays.stream(RoleEnum.values()).map(Enum::name).collect(Collectors.joining(", ")));
            }

            // Step 2: Get the canonical authority name (e.g., "ROLE_USER")
            String authorityName = roleEnumConstant.getAuthority();

            // Step 3: Fetch the Role entity from the database
            Role role = roleRepository.findByName(authorityName)
                    .orElseThrow(() -> new RuntimeException("Configuration error: Role '" + authorityName +
                            "' (defined in RoleEnum) was not found in the database. " +
                            "Please ensure the DataInitializer has run correctly."));
            userRoles.add(role);
        }
        return userRoles;
    }


    @Transactional
    public User createUser(UserCreationRequestDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Error: Username is already taken: " + userDTO.getUsername());
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Error: Email is already in use: " + userDTO.getEmail());
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // This will now throw an exception if roles are invalid or not found
        user.setRoles(getRolesFromNames(userDTO.getRoles()));

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, UserCreationRequestDTO userDetailsDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update username, email, password as before...
        if (userDetailsDTO.getUsername() != null && !userDetailsDTO.getUsername().isEmpty() &&
                !userDetailsDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDetailsDTO.getUsername())) {
                throw new RuntimeException("Error: New username is already taken: " + userDetailsDTO.getUsername());
            }
            user.setUsername(userDetailsDTO.getUsername());
        }
        if (userDetailsDTO.getEmail() != null && !userDetailsDTO.getEmail().isEmpty() &&
                !userDetailsDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetailsDTO.getEmail())) {
                throw new RuntimeException("Error: New email is already in use: " + userDetailsDTO.getEmail());
            }
            user.setEmail(userDetailsDTO.getEmail());
        }
        if (userDetailsDTO.getPassword() != null && !userDetailsDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetailsDTO.getPassword()));
        }

        // Update roles if provided in the DTO
        if (userDetailsDTO.getRoles() != null) {
            // This will throw an exception if roles are invalid or not found
            user.setRoles(getRolesFromNames(userDetailsDTO.getRoles()));
        }

        return userRepository.save(user);
    }

    // ... other methods (getUserById, getAllUsers, deleteUser) remain the same ...
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id + " for deletion.");
        }
        userRepository.deleteById(id);
    }
}