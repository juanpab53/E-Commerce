package com.Ecommerce.PruebaE_Commerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.dto.PagoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PagoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.exceptions.BusinessLogicException;
import com.Ecommerce.PruebaE_Commerce.exceptions.ResourceNotFoundException;
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
    public PagoResponseDTO processarPago(PagoDTO dtopago) {
        Pedido pedido = pedidoRepository.findById(dtopago.pedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede procesar el pago: Pedido no encontrado con ID: " + dtopago.pedidoId()));
        
        if (pedido.getEstado() == Estado.PAGADO) {
            throw new BusinessLogicException("Operación inválida: El pedido con ID " + dtopago.pedidoId() + " ya ha sido pagado.");
        }

        if(pedido.getEstado() == Estado.CANCELADO){
            throw new BusinessLogicException("Operación inválida: No se puede pagar el pedido " + dtopago.pedidoId() + " porque se encuentra CANCELADO.");
        }

        pagoRepository.findByPedidoId(dtopago.pedidoId()).ifPresent(p -> {
            throw new BusinessLogicException("Conflicto: Ya existe un registro de pago asociado al pedido ID: " + dtopago.pedidoId());
        });

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(pedido.getTotal());
        pago.setFechaPago(LocalDateTime.now().toString());
        pago.setMetodoPago(dtopago.metodoPago());
        
        pedido.setEstado(Estado.PAGADO);
        pedidoRepository.save(pedido);

        return mapearAResponse(pagoRepository.save(pago));
    }

    @Transactional
    public PagoResponseDTO buscarPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de pago no encontrado con ID: " + id));
        return mapearAResponse(pago);
    }

    @Transactional
    public PagoResponseDTO buscarPorPedido(Long pedidoId) {
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún pago asociado al pedido ID: " + pedidoId));
        return mapearAResponse(pago);
    }

    @Transactional
    public List<PagoResponseDTO> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    private PagoResponseDTO mapearAResponse(Pago pago) {
        return new PagoResponseDTO(
                pago.getId(),
                pago.getPedido().getId(),
                pago.getMonto(),
                pago.getFechaPago(),
                pago.getMetodoPago().name(), 
                pago.getPedido().getEstado().name()
        );
    }
}