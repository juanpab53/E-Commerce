package com.ecommerce.identity.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecommerce.dto.UserRegistrationDTO;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.identity.domain.Role;
import com.ecommerce.identity.domain.User;
import com.ecommerce.identity.domain.UserRepository;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.shared.domain.valueobject.Address;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserQueryService userQueryService;

    private User exampleUser;
    private UserRegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        Address exampleAddress = new Address();
        exampleAddress.setStreet("Calle Falsa 123");
        exampleAddress.setCity("Medellin");

        exampleUser = new User();
        exampleUser.setId(1L);
        exampleUser.setName("Juan");
        exampleUser.setLastName("Aparicio");
        exampleUser.setEmail("juan@example.com");
        exampleUser.setPassword("hash_encoded");
        exampleUser.setRole(Role.CUSTOMER);
        exampleUser.setAddress(exampleAddress);

        registrationDTO = new UserRegistrationDTO(
                "Juan", "Aparicio", "juan@example.com", "password123",
                "Calle 123", "Medellin", "Colombia", null);
    }

    // --- SEARCH TESTS ---

    @Test
    @DisplayName("Should list all users")
    void listUsersSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(exampleUser));

        List<UserResponseDTO> result = userQueryService.listUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get a user by ID")
    void getUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));

        UserResponseDTO result = userQueryService.getUserById(1L);

        assertEquals("Juan", result.name());
    }

    @Test
    @DisplayName("Should throw NotFound if the ID does not exist")
    void getUserByIdFails() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userQueryService.getUserById(99L));
    }

    @Test
    @DisplayName("Should find a user by email successfully")
    void findByEmailSuccess() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(exampleUser));

        UserResponseDTO result = userQueryService.findByEmail("juan@example.com");

        assertNotNull(result);
        assertEquals("juan@example.com", result.email());
    }

    @Test
    @DisplayName("Should throw an exception if no user is found by email")
    void findByEmailFails() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userQueryService.findByEmail("noexiste@example.com"));
    }

    // --- UPDATE TESTS ---

    @Test
    @DisplayName("Should update user data correctly")
    void updateUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        when(userRepository.save(any(User.class))).thenReturn(exampleUser);

        UserResponseDTO result = userQueryService.update(1L, registrationDTO);

        assertNotNull(result);
        verify(userRepository).save(exampleUser);
    }

    @Test
    @DisplayName("Should fail update if the new email is already in use by another user")
    void updateUserFailsDuplicateEmail() {
        UserRegistrationDTO dtoWithNewEmail = new UserRegistrationDTO(
                "Juan", "Aparicio", "otro@example.com", "password123",
                "Calle 123", "Medellin", "Colombia", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        when(userRepository.existsByEmail("otro@example.com")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> userQueryService.update(1L, dtoWithNewEmail));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw an exception when updating a user that does not exist")
    void updateUserFailsIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userQueryService.update(99L, registrationDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    // --- DELETION TESTS ---

    @Test
    @DisplayName("Should delete a user if it exists")
    void deleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userQueryService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should fail deletion if the user does not exist")
    void deleteUserFails() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userQueryService.deleteUser(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
