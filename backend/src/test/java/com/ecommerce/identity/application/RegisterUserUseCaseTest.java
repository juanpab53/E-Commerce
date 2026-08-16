package com.ecommerce.identity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecommerce.dto.UserRegistrationDTO;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.identity.domain.Role;
import com.ecommerce.identity.domain.User;
import com.ecommerce.identity.domain.UserRepository;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.ValidationException;

@ExtendWith(MockitoExtension.class)
public class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private UserRegistrationDTO registrationDTO(Role role) {
        return new UserRegistrationDTO(
                "Juan", "Aparicio", "juan@example.com", "password123",
                "Colombia", "Medellin", "Calle 123", role);
    }

    private void mockRegistrationSuccess() {
        when(userRepository.existsByEmail("juan@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Should register a customer with the default role and hashed password")
    void registerSuccessDefaultRole() {
        mockRegistrationSuccess();

        UserResponseDTO result = registerUserUseCase.execute(registrationDTO(null));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(Role.CUSTOMER, captor.getValue().getRole());
        assertEquals("hash", captor.getValue().getPassword());
        assertEquals("juan@example.com", captor.getValue().getEmail());
        verify(passwordEncoder).encode("password123");
        assertEquals("juan@example.com", result.email());
    }

    @Test
    @DisplayName("Should fail registration when the email is already in use")
    void registerFailsDuplicateEmail() {
        when(userRepository.existsByEmail("juan@example.com")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> registerUserUseCase.execute(registrationDTO(null)));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration when the email format is invalid")
    void registerFailsInvalidEmail() {
        UserRegistrationDTO invalid = new UserRegistrationDTO(
                "Juan", "Aparicio", "not-an-email", "password123",
                "Colombia", "Medellin", "Calle 123", null);

        assertThrows(ValidationException.class, () -> registerUserUseCase.execute(invalid));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should force CUSTOMER when a non-admin requests the ADMIN role")
    void registerForcesCustomerForNonAdminRequestingAdmin() {
        mockRegistrationSuccess();

        registerUserUseCase.execute(registrationDTO(Role.ADMIN));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(Role.CUSTOMER, captor.getValue().getRole());
    }

    @Test
    @DisplayName("Should preserve ADMIN when the requester is an authenticated admin")
    void registerPreservesAdminForAuthenticatedAdmin() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockRegistrationSuccess();

        registerUserUseCase.execute(registrationDTO(Role.ADMIN));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(Role.ADMIN, captor.getValue().getRole());
    }
}
