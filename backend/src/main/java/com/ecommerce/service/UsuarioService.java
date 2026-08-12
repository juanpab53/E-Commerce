package com.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ecommerce.dto.UsuarioRegistroDTO;
import com.ecommerce.dto.UsuarioResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.shared.domain.valueobject.Address;
import com.ecommerce.model.Rol;
import com.ecommerce.model.Usuario;
import com.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean validarCredenciales(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .map(usuario -> {
                    return passwordEncoder.matches(password, usuario.getPassword());
                })
                .orElse(false);
    }

    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioDto) {
        if (usuarioRepository.existsByEmail(usuarioDto.email())) {
            throw new BusinessRuleException("El email '" + usuarioDto.email() + "' ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDto.nombre());
        usuario.setApellido(usuarioDto.apellido());
        usuario.setEmail(usuarioDto.email());

        String hash = passwordEncoder.encode(usuarioDto.password());
        usuario.setPassword(hash);

        Address dir = new Address();
        dir.setStreet(usuarioDto.calle());
        dir.setCity(usuarioDto.ciudad());
        dir.setCountry(usuarioDto.pais());
        usuario.setAddress(dir);
        usuario.setRol(Rol.CLIENTE);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return mapearAResponse(usuarioGuardado);
    }

    @Transactional
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));
        return mapearAResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No se encontró un usuario con el email: " + email));
        return mapearAResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRegistroDTO dto) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "No se puede actualizar: Usuario no encontrado con ID: " + id));

        if (!usuarioExistente.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new BusinessRuleException(
                    "No se puede actualizar: El email '" + dto.email() + "' ya está en uso por otro usuario.");
        }

        usuarioExistente.setNombre(dto.nombre());
        usuarioExistente.setApellido(dto.apellido());
        usuarioExistente.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isBlank()) {
            usuarioExistente.setPassword(passwordEncoder.encode(dto.password()));
        }

        Address nuevaDir = new Address();
        nuevaDir.setStreet(dto.calle());
        nuevaDir.setCity(dto.ciudad());
        nuevaDir.setCountry(dto.pais());
        usuarioExistente.setAddress(nuevaDir);

        return mapearAResponse(usuarioRepository.save(usuarioExistente));
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NotFoundException("No se puede eliminar: Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO mapearAResponse(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getAddress().toString(),
                u.getRol().toString());
    }
}