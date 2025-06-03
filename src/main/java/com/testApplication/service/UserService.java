package com.testApplication.service;

import com.testApplication.dto.UserCreationRequestDTO;
import com.testApplication.model.Role; // Your Role JPA Entity
import com.testApplication.model.User; // Your User JPA Entity
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    // --- UserDetailsService Implementation ---
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // Assumes role.getName() is "ROLE_USER", etc.
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),       // This is the HASHED password from your database
                true,                     // enabled
                true,                     // accountNonExpired
                true,                     // credentialsNonExpired
                true,                     // accountNonLocked
                authorities               // Authorities collection
        );
    }
    // --- End UserDetailsService Implementation ---

    /**
     * Helper method to convert a set of role strings (like "USER", "ADMIN") from a DTO
     * to a Set of managed Role entities.
     * It assumes RoleEnum defines the valid roles and DataInitializer populates them.
     *
     * @param roleNamesFromDto Set of strings like "USER", "ADMIN"
     * @return Set of managed Role entities
     */
    private Set<Role> getRolesFromNames(Set<String> roleNamesFromDto) {
        Set<Role> userRoles = new HashSet<>();
        if (roleNamesFromDto == null || roleNamesFromDto.isEmpty()) {
            // Default to ROLE_USER if no roles provided in DTO, or handle as error
            Role defaultRole = roleRepository.findByName(RoleEnum.USER.getAuthority())
                    .orElseThrow(() -> new RuntimeException("Default role " + RoleEnum.USER.getAuthority() +
                            " not found in DB. Roles should be pre-initialized by DataInitializer."));
            userRoles.add(defaultRole);
        } else {
            for (String roleNameFromClient : roleNamesFromDto) {
                String authorityName;
                try {
                    // Convert client-provided role name (e.g., "USER") to the canonical authority name (e.g., "ROLE_USER")
                    authorityName = RoleEnum.valueOf(roleNameFromClient.trim().toUpperCase()).getAuthority();
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Invalid role name received from DTO: " + roleNameFromClient +
                            ". This role will be skipped. Ensure DTO sends valid role names like USER, ADMIN.");
                    continue; // Skip this invalid role name
                }

                Role role = roleRepository.findByName(authorityName)
                        .orElseThrow(() -> new RuntimeException("Role not found in DB: " + authorityName +
                                ". Roles should be pre-initialized by DataInitializer."));
                userRoles.add(role);
            }
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
        user.setRoles(getRolesFromNames(userDTO.getRoles()));

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) { // Different from loadUserByUsername for UserDetailsService
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, UserCreationRequestDTO userDetailsDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update username if provided and changed, checking for uniqueness
        if (userDetailsDTO.getUsername() != null && !userDetailsDTO.getUsername().isEmpty() &&
                !userDetailsDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDetailsDTO.getUsername())) {
                throw new RuntimeException("Error: New username is already taken: " + userDetailsDTO.getUsername());
            }
            user.setUsername(userDetailsDTO.getUsername());
        }

        // Update email if provided and changed, checking for uniqueness
        if (userDetailsDTO.getEmail() != null && !userDetailsDTO.getEmail().isEmpty() &&
                !userDetailsDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetailsDTO.getEmail())) {
                throw new RuntimeException("Error: New email is already in use: " + userDetailsDTO.getEmail());
            }
            user.setEmail(userDetailsDTO.getEmail());
        }

        // Update password if a new one is provided (and not empty)
        if (userDetailsDTO.getPassword() != null && !userDetailsDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetailsDTO.getPassword()));
        }

        // Update roles if provided
        if (userDetailsDTO.getRoles() != null) { // Check if DTO intends to update roles
            user.setRoles(getRolesFromNames(userDetailsDTO.getRoles()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id + " for deletion.");
        }
        userRepository.deleteById(id);
    }
}