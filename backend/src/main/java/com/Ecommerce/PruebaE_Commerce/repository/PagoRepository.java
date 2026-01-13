package com.Ecommerce.PruebaE_Commerce.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Ecommerce.PruebaE_Commerce.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByPedidoId(Long pedidoId);
}
