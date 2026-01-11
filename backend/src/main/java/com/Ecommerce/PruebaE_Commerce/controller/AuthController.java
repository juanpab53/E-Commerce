package com.Ecommerce.PruebaE_Commerce.controller;

import com.Ecommerce.PruebaE_Commerce.model.Usuario;
import com.Ecommerce.PruebaE_Commerce.service.UsuarioService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        if (passwordEncoder.matches(password, usuario.getPassword())) {
            return ResponseEntity.ok("Login exitoso. ¡Bienvenido " + usuario.getNombre() + "!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }
    }
}