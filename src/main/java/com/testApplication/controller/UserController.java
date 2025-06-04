package com.testApplication.controller;

import com.testApplication.dto.UserLegacy;
import com.testApplication.service.UserServiceLegacy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserServiceLegacy userServiceLegacy;


    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserLegacy> addUsers(@RequestBody UserLegacy userLegacy) {
        userServiceLegacy.addUser(userLegacy);
        return new ResponseEntity<>(userLegacy, HttpStatus.CREATED);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<UserLegacy>> getAllUsers() {
        List<UserLegacy> userLegacies = userServiceLegacy.getAllUsers();
        return new ResponseEntity<>(userLegacies, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UserLegacy> getUserById(@PathVariable int id) {
        Optional<UserLegacy> user = userServiceLegacy.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserLegacy> updateUser(@PathVariable int id, @RequestBody UserLegacy newUserLegacy) {
        boolean updated = userServiceLegacy.updateUser(id, newUserLegacy);
        if (updated) {
            return new ResponseEntity<>(newUserLegacy, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        boolean deleted = userServiceLegacy.deleteUser(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "UP";
    }

}
