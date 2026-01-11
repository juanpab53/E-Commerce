package com.Ecommerce.PruebaE_Commerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.Ecommerce.PruebaE_Commerce.model.Usuario;
import com.Ecommerce.PruebaE_Commerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    private UserRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void debeEncriptarPasswordAlRegistrar() {
        Usuario usuario = new Usuario();
        usuario.setPassword("12345");

        when(passwordEncoder.encode("12345")).thenReturn("password_encriptada_abc");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArguments()[0]);

        Usuario resultado = usuarioService.registrarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals("password_encriptada_abc", resultado.getPassword()); 
        verify(usuarioRepository, times(1)).save(any(Usuario.class)); 
    }
}
