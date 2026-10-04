package com.vaishnavipawar.tasktrack.controllers;

import com.vaishnavipawar.tasktrack.entities.User;
import com.vaishnavipawar.tasktrack.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {

        User user = authService.register(
                request.name(),
                request.email(),
                request.password(),
                request.role()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthService.LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        AuthService.LoginResponse response =
                authService.login(
                        request.email(),
                        request.password()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {

        authService.logout();

        return ResponseEntity.ok("Logged out successfully");
    }

    public record RegisterRequest(
            String name,
            String email,
            String password,
            User.Role role
    ) {
    }

    public record LoginRequest(
            String email,
            String password
    ) {
    }
}
