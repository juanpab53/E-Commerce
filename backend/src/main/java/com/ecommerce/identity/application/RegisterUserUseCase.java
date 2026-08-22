package com.ecommerce.identity.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.UserRegistrationDTO;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.identity.domain.Role;
import com.ecommerce.identity.domain.User;
import com.ecommerce.identity.domain.UserRepository;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.valueobject.Address;
import com.ecommerce.shared.domain.valueobject.Email;

@RequiredArgsConstructor
@Service
public class RegisterUserUseCase {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO execute(UserRegistrationDTO dto) {
        Email email = new Email(dto.email());

        if (userRepository.existsByEmail(email.value())) {
            throw new BusinessRuleException("Email is already in use: " + email.value());
        }

        User user = new User();
        user.setName(dto.name());
        user.setLastName(dto.lastName());
        user.setEmail(email.value());
        user.setPassword(passwordEncoder.encode(dto.password()));

        Address address = new Address();
        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setCountry(dto.country());
        user.setAddress(address);

        user.setRole(resolveRole(dto.role()));

        return toResponse(userRepository.save(user));
    }

    private Role resolveRole(Role requestedRole) {
        if (requestedRole == null || requestedRole == Role.CUSTOMER) {
            return Role.CUSTOMER;
        }
        if (isAuthenticatedAdmin()) {
            return Role.ADMIN;
        }
        return Role.CUSTOMER;
    }

    private boolean isAuthenticatedAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ADMIN_AUTHORITY::equals);
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
