package com.example.laboratornie.controller;

import com.example.laboratornie.DTO.*;
import com.example.laboratornie.model.User;
import com.example.laboratornie.model.UserSession;
import com.example.laboratornie.repository.UserRepository;
import com.example.laboratornie.repository.UserSessionRepository;
import com.example.laboratornie.service.TokenService;
import com.example.laboratornie.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder; // Добавляем PasswordEncoder

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Map<String, String> tokens = tokenService.generateTokenPair(userDetails, request.getDeviceId());

            log.info("User {} successfully logged in", request.getUsername());
            return ResponseEntity.ok(tokens);

        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            Map<String, String> tokens = tokenService.refreshTokens(request.getRefreshToken());
            return ResponseEntity.ok(tokens);

        } catch (SecurityException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Token refresh failed"));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole(),
                    passwordEncoder
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
        log.info("Администратор запросил список пользователей. Всего: {}", users.size());
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
            log.info("Администратор удалил пользователя с ID: {}", id);
            return ResponseEntity.ok(Map.of("message", "Пользователь успешно удален."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(user -> ResponseEntity.ok(Map.of(
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "role", user.getRole()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            tokenService.revokeAllUserSessions(userEmail);

            log.info("User {} logged out", userEmail);
            return ResponseEntity.ok(Map.of("message", "Successfully logged out"));

        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Logout failed"));
        }
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<UserSession>> getUserSessions() {
        List<UserSession> sessions = userSessionRepository.findAll();
        log.info("Запрошен список сессий. Всего: {}", sessions.size());
        return ResponseEntity.ok(sessions);
    }
}