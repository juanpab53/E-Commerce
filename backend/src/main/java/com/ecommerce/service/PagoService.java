package com.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ecommerce.dto.PagoDTO;
import com.ecommerce.dto.PagoResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.Estado;
import com.ecommerce.model.Pago;
import com.ecommerce.model.Pedido;
import com.ecommerce.repository.PagoRepository;
import com.ecommerce.repository.PedidoRepository;
import jakarta.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    
    private final PedidoRepository pedidoRepository;

    @Transactional 
    public PagoResponseDTO procesarPago(PagoDTO dtopago) {
        Pedido pedido = pedidoRepository.findById(dtopago.pedidoId())
                .orElseThrow(() -> new NotFoundException("No se puede procesar el pago: Pedido no encontrado con ID: " + dtopago.pedidoId()));
        
        if (pedido.getEstado() == Estado.PAGADO) {
            throw new BusinessRuleException("Operación inválida: El pedido con ID " + dtopago.pedidoId() + " ya ha sido pagado.");
        }

        if(pedido.getEstado() == Estado.CANCELADO){
            throw new BusinessRuleException("Operación inválida: No se puede pagar el pedido " + dtopago.pedidoId() + " porque se encuentra CANCELADO.");
        }

        pagoRepository.findByPedidoId(dtopago.pedidoId()).ifPresent(p -> {
            throw new BusinessRuleException("Conflicto: Ya existe un registro de pago asociado al pedido ID: " + dtopago.pedidoId());
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
                .orElseThrow(() -> new NotFoundException("Registro de pago no encontrado con ID: " + id));
        return mapearAResponse(pago);
    }

    @Transactional
    public PagoResponseDTO buscarPorPedido(Long pedidoId) {
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new NotFoundException("No se encontró ningún pago asociado al pedido ID: " + pedidoId));
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