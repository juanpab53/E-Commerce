package com.Ecommerce.PruebaE_Commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Ecommerce.PruebaE_Commerce.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long id);
}
