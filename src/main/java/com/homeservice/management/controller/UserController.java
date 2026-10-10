package com.homeservice.management.controller;

import com.homeservice.management.dto.SignupRequest;
import com.homeservice.management.dto.UserResponseDTO;
import com.homeservice.management.entity.User;
import com.homeservice.management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @RequestBody SignupRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole());

        User savedUser =
                userService.createUser(
                        user,
                        request.getSpecialization()
                );

        return ResponseEntity.ok(toDTO(savedUser));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        List<UserResponseDTO> users =
                userService.getAllUsers()
                        .stream()
                        .map(this::toDTO)
                        .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        try {

            User updatedUser =
                    userService.updateUser(id, user);

            return ResponseEntity.ok(
                    toDTO(updatedUser)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        try {

            userService.deleteUser(id);

            return ResponseEntity.ok(
                    "User deleted successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    private UserResponseDTO toDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole()
        );
    }
}