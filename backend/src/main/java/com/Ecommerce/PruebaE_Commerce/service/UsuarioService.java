package com.Ecommerce.PruebaE_Commerce.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.model.Usuario;
import com.Ecommerce.PruebaE_Commerce.repository.UserRepository;

@Service
public class UsuarioService {
    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(Usuario usuario) {
        String hash = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(hash);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

}
