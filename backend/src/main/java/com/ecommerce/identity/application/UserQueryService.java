package com.ecommerce.identity.application;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.UserRegistrationDTO;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.identity.domain.User;
import com.ecommerce.identity.domain.UserRepository;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.shared.domain.valueobject.Address;

@RequiredArgsConstructor
@Service
public class UserQueryService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public List<UserResponseDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
        return toResponse(user);
    }

    @Transactional
    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No user found with the email: " + email));
        return toResponse(user);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRegistrationDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot update: User not found with ID: " + id));

        if (!existingUser.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new BusinessRuleException(
                    "Cannot update: The email '" + dto.email() + "' is already in use by another user.");
        }

        existingUser.setName(dto.name());
        existingUser.setLastName(dto.lastName());
        existingUser.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.password()));
        }

        Address newAddress = new Address();
        newAddress.setStreet(dto.street());
        newAddress.setCity(dto.city());
        newAddress.setCountry(dto.country());
        existingUser.setAddress(newAddress);

        return toResponse(userRepository.save(existingUser));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Cannot delete: User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAddress().toString(),
                user.getRole().toString());
    }
}
