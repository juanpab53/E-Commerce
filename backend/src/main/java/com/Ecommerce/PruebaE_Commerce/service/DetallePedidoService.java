package com.Ecommerce.PruebaE_Commerce.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.model.DetallePedido;
import com.Ecommerce.PruebaE_Commerce.repository.DetallePedidoRepository;

@Service
public class DetallePedidoService {
    @Autowired
    private DetallePedidoRepository detalleRepository;

    public List<DetallePedido> listarPorPedido(Long idPedido) {
        return detalleRepository.findByPedidoId(idPedido);
    }
}
