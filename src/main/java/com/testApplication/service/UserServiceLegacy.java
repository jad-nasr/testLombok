package com.testApplication.service;

import com.testApplication.dto.UserLegacy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceLegacy {

    //RDS DB
    private final List<UserLegacy> userLegacies = new ArrayList<>();

    // Create a new user
    public void addUser(UserLegacy userLegacy) {
        userLegacies.add(userLegacy);
    }

    // Retrieve all users
    public List<UserLegacy> getAllUsers() {
        return userLegacies;
    }

    // Retrieve a user by id
    public Optional<UserLegacy> getUserById(int id) {
        return userLegacies.stream()
                .filter(userLegacy -> userLegacy.getId() == id)
                .findFirst();
    }

    // Update a user
    public boolean updateUser(int id, UserLegacy newUserLegacy) {
        return getUserById(id).map(existingUserLegacy -> {
            userLegacies.remove(existingUserLegacy);
            userLegacies.add(newUserLegacy);
            return true;
        }).orElse(false);
    }

    // Delete a user by id
    public boolean deleteUser(int id) {
        return userLegacies
                .removeIf(userLegacy -> userLegacy.getId() == id);
    }
}
