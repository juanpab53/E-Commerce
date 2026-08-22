package com.ecommerce.identity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.identity.domain.Role;
import com.ecommerce.identity.domain.User;
import com.ecommerce.identity.domain.UserRepository;
import com.ecommerce.identity.infrastructure.security.JwtService;
import com.ecommerce.identity.infrastructure.security.LoginResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;

@ExtendWith(MockitoExtension.class)
public class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User exampleUser;

    @BeforeEach
    void setUp() {
        exampleUser = new User();
        exampleUser.setName("Juan");
        exampleUser.setEmail("juan@example.com");
        exampleUser.setPassword("hash_encoded");
        exampleUser.setRole(Role.CUSTOMER);
    }

    @Test
    @DisplayName("Should return a token when credentials are valid")
    void loginSuccess() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(exampleUser));
        when(passwordEncoder.matches("password123", "hash_encoded")).thenReturn(true);
        when(jwtService.generateAccessToken("juan@example.com")).thenReturn("token-123");

        LoginResponseDTO result = loginUseCase.execute(new LoginRequestDTO("juan@example.com", "password123"));

        assertEquals("Juan", result.username());
        assertEquals("token-123", result.token());
        verify(jwtService).generateAccessToken("juan@example.com");
    }

    @Test
    @DisplayName("Should throw an exception when the user does not exist")
    void loginFailsUserNotFound() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class,
                () -> loginUseCase.execute(new LoginRequestDTO("noexiste@example.com", "password123")));
        verify(jwtService, never()).generateAccessToken(anyString());
    }

    @Test
    @DisplayName("Should throw an exception when the password is incorrect")
    void loginFailsWrongPassword() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(exampleUser));
        when(passwordEncoder.matches("wrongpass", "hash_encoded")).thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> loginUseCase.execute(new LoginRequestDTO("juan@example.com", "wrongpass")));
        verify(jwtService, never()).generateAccessToken(anyString());
    }
}
