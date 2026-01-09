package com.Ecommerce.PruebaE_Commerce.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Ecommerce.PruebaE_Commerce.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByNombreIgnoreCase(String nombre);
}
