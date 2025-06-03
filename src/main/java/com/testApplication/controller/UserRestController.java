package com.testApplication.controller;

import com.testApplication.dto.UserCreationRequestDTO;
import com.testApplication.dto.UserResponseDTO;
import com.testApplication.model.Role; // Import Role
import com.testApplication.model.User;
import com.testApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set; // Import Set
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // Updated helper method to map User entity to UserResponseDTO
    private UserResponseDTO convertToResponseDTO(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName) // Get the name from each Role entity
                .collect(Collectors.toSet());
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roleNames // Pass the set of role names
        );
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreationRequestDTO userCreationRequestDTO) {
        try {
            User createdUser = userService.createUser(userCreationRequestDTO);
            return new ResponseEntity<>(convertToResponseDTO(createdUser), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Consider more specific error DTOs or using @ControllerAdvice for exception handling
            return ResponseEntity.badRequest().body(null); // Or build a proper error response
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(convertToResponseDTO(user), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(this::convertToResponseDTO) // this::convertToResponseDTO still works
                .collect(Collectors.toList());
        return new ResponseEntity<>(userResponseDTOs, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserCreationRequestDTO userDetailsDTO) {
        try {
            User updatedUser = userService.updateUser(id, userDetailsDTO);
            return new ResponseEntity<>(convertToResponseDTO(updatedUser), HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ... deleteUser method remains the same ...
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) { // Simple check, refine this
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}