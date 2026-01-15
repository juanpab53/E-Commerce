package com.Ecommerce.PruebaE_Commerce.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.dto.UsuarioRegistroDTO;
import com.Ecommerce.PruebaE_Commerce.dto.UsuarioResponseDTO;
import com.Ecommerce.PruebaE_Commerce.exceptions.BusinessLogicException;
import com.Ecommerce.PruebaE_Commerce.exceptions.ResourceNotFoundException;
import com.Ecommerce.PruebaE_Commerce.model.Direccion;
import com.Ecommerce.PruebaE_Commerce.model.Rol;
import com.Ecommerce.PruebaE_Commerce.model.Usuario;
import com.Ecommerce.PruebaE_Commerce.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioDto) {
        if (usuarioRepository.existsByEmail(usuarioDto.email())) {
            throw new BusinessLogicException("El email '" + usuarioDto.email() + "' ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDto.nombre());
        usuario.setApellido(usuarioDto.apellido());
        usuario.setEmail(usuarioDto.email());

        String hash = passwordEncoder.encode(usuarioDto.password());
        usuario.setPassword(hash);

        Direccion dir = new Direccion();
        dir.setCalle(usuarioDto.calle());
        dir.setCiudad(usuarioDto.ciudad());
        dir.setPais(usuarioDto.pais());
        usuario.setDireccion(dir);
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
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return mapearAResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un usuario con el email: " + email));
        return mapearAResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRegistroDTO dto) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Usuario no encontrado con ID: " + id));

        if (!usuarioExistente.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new BusinessLogicException("No se puede actualizar: El email '" + dto.email() + "' ya está en uso por otro usuario.");
        }

        usuarioExistente.setNombre(dto.nombre());
        usuarioExistente.setApellido(dto.apellido()); 
        usuarioExistente.setEmail(dto.email());
        
        if (dto.password() != null && !dto.password().isBlank()) {
            usuarioExistente.setPassword(passwordEncoder.encode(dto.password()));
        }

        Direccion nuevaDir = new Direccion();
        nuevaDir.setCalle(dto.calle());
        nuevaDir.setCiudad(dto.ciudad());
        nuevaDir.setPais(dto.pais());
        usuarioExistente.setDireccion(nuevaDir);

        return mapearAResponse(usuarioRepository.save(usuarioExistente));
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO mapearAResponse(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getDireccion().toString(),
                u.getRol().toString());
    }
}