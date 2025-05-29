package com.testApplication.service;

import com.testApplication.dto.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    //RDS DB
    private final List<User> users = new ArrayList<>();

    // Create a new user
    public void addUser(User user) {
        users.add(user);
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        return users;
    }

    // Retrieve a user by id
    public Optional<User> getUserById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    // Update a user
    public boolean updateUser(int id, User newUser) {
        return getUserById(id).map(existingUser -> {
            users.remove(existingUser);
            users.add(newUser);
            return true;
        }).orElse(false);
    }

    // Delete a user by id
    public boolean deleteUser(int id) {
        return users
                .removeIf(user -> user.getId() == id);
    }
}
