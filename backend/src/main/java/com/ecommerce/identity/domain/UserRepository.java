package com.ecommerce.identity.domain;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    User save(User user);

    List<User> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);
}
