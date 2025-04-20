package com.example.messaging.controller;

import com.example.messaging.dto.user.UserRequestDTO;
import com.example.messaging.dto.user.UserResponseDTO;
import com.example.messaging.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.createUser(userRequestDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @GetMapping(params = "username")
    public ResponseEntity<UserResponseDTO> getUserByUsername(
            @RequestParam(value = "username", required = true) String username) {
        Optional<UserResponseDTO> userResponseDTO = userService.findByUsername(username)
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail()));
        return userResponseDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @RequestParam(value = "email", required = true) String email) {
        Optional<UserResponseDTO> userResponseDTO = userService.findByEmail(email);
        return userResponseDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        Optional<UserResponseDTO> userResponseDTO = userService.findById(id)
                .map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail()));
        return userResponseDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}