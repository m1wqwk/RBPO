package com.example.laboratornie.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PagesController {

    @GetMapping("/")
    public ResponseEntity<?> index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            System.out.println("Authenticate!");

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "error", "Unauthorized",
                            "message", "Authenticate!"
                    ));
        }

        String username = authentication.getName();
        return ResponseEntity.ok(Map.of(
                "message", "Welcome!",
                "username", username,
                "authenticated", true
        ));
    }
}