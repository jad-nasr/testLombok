package com.testApplication.service;

// In your test class, e.g., UserServiceWithSpringBootTest.java
import org.springframework.boot.test.context.SpringBootTest;
// Import the new @MockitoBean
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.testApplication.service.UserService;
import com.testApplication.repository.UserRepository;
import com.testApplication.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito; // Still needed for when() etc.

import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals; // Make sure this is imported

@SpringBootTest
public class UserServiceWithSpringBootTest {

    @Autowired
    private UserService userService;

    // Use @MockitoBean directly on the field
    @MockitoBean
    private UserRepository userRepository;

    @Test
    void testSomeUserServiceMethod() {
        // Arrange: Define mock behavior
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        // userRepository is already a mock here, injected by Spring
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Act: Call the service method
        Optional<User> result = userService.getUserByUsername("testuser");

        // Assert: Verify the outcome and interactions
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername()); // Ensure assertEquals is imported
        Mockito.verify(userRepository).findByUsername("testuser");
    }
}