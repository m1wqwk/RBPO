package com.example.laboratornie.controller;

import com.example.laboratornie.DTO.RegistrationRequest;
import com.example.laboratornie.model.User;
import com.example.laboratornie.repository.UserRepository;
import com.example.laboratornie.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Пользователь успешно зарегистрирован.",
                    "username", user.getUsername(),
                    "role", user.getRole()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        System.out.println("Администратор запросил список пользователей. Всего: " + users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            System.out.println("Администратор удалил пользователя с ID: " + id);
            return ResponseEntity.ok(Map.of("message", "Пользователь успешно удален."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(Map.of(
                "message", "Используйте обычную аутентификацию для получения текущей информации о пользователе.",
                "instruction", "Отправить запрос с заголовком авторизации."
        ));
    }
}