package com.ecommerce.controller;

import com.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO loginRequest) {

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BusinessRuleException("Invalid credentials: User not found"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BusinessRuleException("Invalid credentials: Incorrect password");
        }

        return ResponseEntity.ok("Login successful. Welcome " + user.getName() + "!");
    }
}
