package com.ecommerce.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.ecommerce.dto.UsuarioRegistroDTO;
import com.ecommerce.dto.UsuarioResponseDTO;
import com.ecommerce.exceptions.BusinessLogicException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Direccion;
import com.ecommerce.model.Rol;
import com.ecommerce.model.Usuario;
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
public class UsuarioServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;
    private UsuarioRegistroDTO registroDTO;

    @BeforeEach
    void setUp() {
        Direccion direccionEjemplo = new Direccion();
        direccionEjemplo.setCalle("Calle Falsa 123");
        direccionEjemplo.setCiudad("Medellin");

        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setNombre("Juan");
        usuarioEjemplo.setApellido("Aparicio");
        usuarioEjemplo.setEmail("juan@example.com");
        usuarioEjemplo.setPassword("hash_encoded");
        usuarioEjemplo.setRol(Rol.CLIENTE); 
        usuarioEjemplo.setDireccion(direccionEjemplo); 

        registroDTO = new UsuarioRegistroDTO(
                "Juan", "Aparicio", "juan@example.com", "password123",
                "Calle 123", "Medellin", "Colombia");
    }

    // --- PRUEBAS DE REGISTRO ---

    @Test
    @DisplayName("Debe registrar usuario exitosamente")
    void registrarUsuarioExito() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash_encoded");
        when(userRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        UsuarioResponseDTO resultado = usuarioService.registrarUsuario(registroDTO);

        assertNotNull(resultado);
        assertEquals(usuarioEjemplo.getEmail(), resultado.email());
        verify(userRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe fallar registro si el email ya existe")
    void registrarUsuarioFallaEmailDuplicado() {
        when(userRepository.existsByEmail(registroDTO.email())).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> usuarioService.registrarUsuario(registroDTO));
        verify(userRepository, never()).save(any(Usuario.class));
    }

    // --- PRUEBAS DE VALIDACIÓN ---

    @Test
    @DisplayName("Debe validar credenciales correctamente")
    void validarCredencialesExito() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches("password123", "hash_encoded")).thenReturn(true);

        boolean esValido = usuarioService.validarCredenciales("juan@example.com", "password123");

        assertTrue(esValido);
    }

    @Test
    @DisplayName("Debe retornar false si la contraseña es incorrecta")
    void validarCredencialesFallaPassword() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuarioEjemplo));
        when(passwordEncoder.matches("wrongpass", "hash_encoded")).thenReturn(false);

        boolean esValido = usuarioService.validarCredenciales("juan@example.com", "wrongpass");

        assertFalse(esValido);
    }

    @Test
    @DisplayName("Debe retornar false si el usuario no existe para validar credenciales")
    void validarCredencialesFallaUsuarioNoExiste() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        boolean esValido = usuarioService.validarCredenciales("noexiste@example.com", "password123");

        assertFalse(esValido);
    }

    // --- PRUEBAS DE BÚSQUEDA ---

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void listarUsuariosExito() {
        when(userRepository.findAll()).thenReturn(List.of(usuarioEjemplo));

        List<UsuarioResponseDTO> resultado = usuarioService.listarUsuarios();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Debe obtener usuario por ID")
    void obtenerPorIdExito() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        UsuarioResponseDTO resultado = usuarioService.obtenerUsuarioPorId(1L);

        assertEquals("Juan", resultado.nombre());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFound si el ID no existe")
    void obtenerPorIdFalla() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.obtenerUsuarioPorId(99L));
    }

    @Test
    @DisplayName("Debe buscar usuario por email exitosamente")
    void buscarPorEmailExito() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(usuarioEjemplo));

        UsuarioResponseDTO resultado = usuarioService.buscarPorEmail("juan@example.com");

        assertNotNull(resultado);
        assertEquals("juan@example.com", resultado.email());
    }

    @Test
    @DisplayName("Debe lanzar excepción si no encuentra usuario por email")
    void buscarPorEmailFalla() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorEmail("noexiste@example.com"));
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Debe actualizar datos de usuario correctamente")
    void actualizarUsuarioExito() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(userRepository.save(any(Usuario.class))).thenReturn(usuarioEjemplo);

        UsuarioResponseDTO resultado = usuarioService.actualizar(1L, registroDTO);

        assertNotNull(resultado);
        verify(userRepository).save(usuarioEjemplo);
    }

    @Test
    @DisplayName("Debe fallar actualización si el nuevo email ya está en uso por otro usuario")
    void actualizarUsuarioFallaEmailDuplicado() {
        UsuarioRegistroDTO dtoConNuevoEmail = new UsuarioRegistroDTO(
                "Juan", "Aparicio", "otro@example.com", "password123",
                "Calle 123", "Medellin", "Colombia");

        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(userRepository.existsByEmail("otro@example.com")).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> usuarioService.actualizar(1L, dtoConNuevoEmail));
        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si el usuario no existe")
    void actualizarUsuarioFallaIdNoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.actualizar(99L, registroDTO));
        verify(userRepository, never()).save(any(Usuario.class));
    }

    // --- PRUEBAS DE ELIMINACIÓN ---

    @Test
    @DisplayName("Debe eliminar usuario si existe")
    void eliminarUsuarioExito() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> usuarioService.eliminarUsuario(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe fallar eliminación si el usuario no existe")
    void eliminarUsuarioFalla() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.eliminarUsuario(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}