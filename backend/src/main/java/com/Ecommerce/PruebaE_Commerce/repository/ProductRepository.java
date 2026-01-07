package com.Ecommerce.PruebaE_Commerce.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Ecommerce.PruebaE_Commerce.model.Producto;

public interface ProductRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoria(Long categoria);
}
