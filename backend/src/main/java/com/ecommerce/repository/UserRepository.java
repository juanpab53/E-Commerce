package com.ecommerce.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.model.Usuario;

public interface UserRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
