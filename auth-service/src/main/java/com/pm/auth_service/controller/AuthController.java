package com.pm.auth_service.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.dto.LoginResponseDTO;
import com.pm.auth_service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login endpoint", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        // Authentication logic here
        Optional<String> token = authService.authenticate(loginRequestDTO);
        if (token.isPresent()) {
            return ResponseEntity.ok(new LoginResponseDTO(token.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO("Invalid credentials"));
        }
    }

    @Operation(summary = "Validate token endpoint", description = "Validate JWT token and return user details")
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        // Validation logic here
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (authService.validateToken(token)) {
                return ResponseEntity.ok().body("Token is valid");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }
}
