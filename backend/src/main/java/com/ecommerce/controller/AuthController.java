package com.ecommerce.controller;

import com.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.model.Usuario;
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

    private final UserRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO loginRequest) {

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas: Usuario no encontrado"));

        if (!passwordEncoder.matches(loginRequest.password(), usuario.getPassword())) {
            throw new BusinessRuleException("Credenciales inválidas: Contraseña incorrecta");
        }

        return ResponseEntity.ok("Login exitoso. ¡Bienvenido " + usuario.getNombre() + "!");
    }
}