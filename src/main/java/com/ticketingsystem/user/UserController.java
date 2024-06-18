package com.ticketingsystem.user;

import com.ticketingsystem.error.ResourceNotAvailableException;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path="/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
    }

    @PostMapping()
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User originalDBUser = userRepository.findByEmail(user.getEmail());
        return ResponseEntity.status(originalDBUser == null ? HttpStatus.CREATED : HttpStatus.OK).body(userRepository.add(user));
    }

    @GetMapping(path = "{email}")
    public ResponseEntity<User> getUser(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotAvailableException("User Not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userRepository.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
