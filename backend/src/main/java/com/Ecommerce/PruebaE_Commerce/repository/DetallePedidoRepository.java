package com.Ecommerce.PruebaE_Commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Ecommerce.PruebaE_Commerce.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    List<DetallePedido> findByPedidoId(Long idPedido);
}
