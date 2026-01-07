package com.Ecommerce.PruebaE_Commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.model.Estado;
import com.Ecommerce.PruebaE_Commerce.model.Pago;
import com.Ecommerce.PruebaE_Commerce.model.Pedido;
import com.Ecommerce.PruebaE_Commerce.repository.PagoRepository;
import com.Ecommerce.PruebaE_Commerce.repository.PedidoRepository;
import jakarta.transaction.Transactional;

@Service
public class PagoService {
    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional 
    public Pago registrarPago(Pago pago) {

        Pedido pedido = pedidoRepository.findById(pago.getPedido().getId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        pedido.setEstado(Estado.PAGADO); 
        pedidoRepository.save(pedido);

        return pagoRepository.save(pago);
    }

    public Pago obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id).orElse(null);
    }

    public Pago actualizarPago(Pago pago) {
        return pagoRepository.save(pago);
    }

    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }


}
