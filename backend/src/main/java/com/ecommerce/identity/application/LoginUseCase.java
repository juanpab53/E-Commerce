package com.ecommerce.identity.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.identity.domain.User;
import com.ecommerce.identity.domain.UserRepository;
import com.ecommerce.identity.infrastructure.security.JwtService;
import com.ecommerce.identity.infrastructure.security.LoginResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;

@RequiredArgsConstructor
@Service
public class LoginUseCase {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public LoginResponseDTO execute(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessRuleException("Invalid credentials: User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessRuleException("Invalid credentials: Incorrect password");
        }

        String token = jwtService.generateAccessToken(user.getEmail());
        return new LoginResponseDTO(user.getName(), token, "Login successful");
    }
}
