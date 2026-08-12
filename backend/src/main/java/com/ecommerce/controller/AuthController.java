package com.ecommerce.controller;

import com.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.exceptions.BusinessLogicException;
import com.ecommerce.model.Usuario;
import com.ecommerce.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
private PasswordEncoder passwordEncoder; 

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO loginRequest) {

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BusinessLogicException("Credenciales inválidas: Usuario no encontrado"));

        if (!passwordEncoder.matches(loginRequest.password(), usuario.getPassword())) {
            throw new BusinessLogicException("Credenciales inválidas: Contraseña incorrecta");
        }

        return ResponseEntity.ok("Login exitoso. ¡Bienvenido " + usuario.getNombre() + "!");
    }
}