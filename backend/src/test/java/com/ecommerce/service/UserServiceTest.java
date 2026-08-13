package com.ecommerce.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.ecommerce.dto.UserRegistrationDTO;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.shared.domain.valueobject.Address;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
                "Calle 123", "Medellin", "Colombia");
    }

    // --- REGISTRATION TESTS ---

    @Test
    @DisplayName("Should register a user successfully")
    void registerUserSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash_encoded");
        when(userRepository.save(any(User.class))).thenReturn(exampleUser);

        UserResponseDTO result = userService.registerUser(registrationDTO);

        assertNotNull(result);
        assertEquals(exampleUser.getEmail(), result.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration if the email already exists")
    void registerUserFailsDuplicateEmail() {
        when(userRepository.existsByEmail(registrationDTO.email())).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> userService.registerUser(registrationDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    // --- VALIDATION TESTS ---

    @Test
    @DisplayName("Should validate credentials correctly")
    void validateCredentialsSuccess() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(exampleUser));
        when(passwordEncoder.matches("password123", "hash_encoded")).thenReturn(true);

        boolean isValid = userService.validateCredentials("juan@example.com", "password123");

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false if the password is incorrect")
    void validateCredentialsFailsWrongPassword() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(exampleUser));
        when(passwordEncoder.matches("wrongpass", "hash_encoded")).thenReturn(false);

        boolean isValid = userService.validateCredentials("juan@example.com", "wrongpass");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false if the user does not exist for credential validation")
    void validateCredentialsFailsUserNotFound() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        boolean isValid = userService.validateCredentials("noexiste@example.com", "password123");

        assertFalse(isValid);
    }

    // --- SEARCH TESTS ---

    @Test
    @DisplayName("Should list all users")
    void listUsersSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(exampleUser));

        List<UserResponseDTO> result = userService.listUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should get a user by ID")
    void getUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));

        UserResponseDTO result = userService.getUserById(1L);

        assertEquals("Juan", result.name());
    }

    @Test
    @DisplayName("Should throw NotFound if the ID does not exist")
    void getUserByIdFails() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("Should find a user by email successfully")
    void findByEmailSuccess() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(exampleUser));

        UserResponseDTO result = userService.findByEmail("juan@example.com");

        assertNotNull(result);
        assertEquals("juan@example.com", result.email());
    }

    @Test
    @DisplayName("Should throw an exception if no user is found by email")
    void findByEmailFails() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByEmail("noexiste@example.com"));
    }

    // --- UPDATE TESTS ---

    @Test
    @DisplayName("Should update user data correctly")
    void updateUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        when(userRepository.save(any(User.class))).thenReturn(exampleUser);

        UserResponseDTO result = userService.update(1L, registrationDTO);

        assertNotNull(result);
        verify(userRepository).save(exampleUser);
    }

    @Test
    @DisplayName("Should fail update if the new email is already in use by another user")
    void updateUserFailsDuplicateEmail() {
        UserRegistrationDTO dtoWithNewEmail = new UserRegistrationDTO(
                "Juan", "Aparicio", "otro@example.com", "password123",
                "Calle 123", "Medellin", "Colombia");

        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        when(userRepository.existsByEmail("otro@example.com")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> userService.update(1L, dtoWithNewEmail));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw an exception when updating a user that does not exist")
    void updateUserFailsIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(99L, registrationDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    // --- DELETION TESTS ---

    @Test
    @DisplayName("Should delete a user if it exists")
    void deleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should fail deletion if the user does not exist")
    void deleteUserFails() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
