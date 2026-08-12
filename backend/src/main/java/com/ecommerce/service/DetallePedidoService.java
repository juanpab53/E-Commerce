package com.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ecommerce.dto.DetallePedidoResponseDTO;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.DetallePedido;
import com.ecommerce.repository.DetallePedidoRepository;
import jakarta.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class DetallePedidoService {
    private final DetallePedidoRepository detalleRepository;

    @Transactional
    public DetallePedidoResponseDTO buscarPorId(Long id) {
        DetallePedido detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detalle de pedido no encontrado con ID: " + id));
        return mapearAResponse(detalle);
    }

    @Transactional
    public List<DetallePedidoResponseDTO> listarPorPedido(Long pedidoId) {
        return detalleRepository.findByPedidoId(pedidoId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    private DetallePedidoResponseDTO mapearAResponse(DetallePedido d) {
        return new DetallePedidoResponseDTO(
                d.getId(),
                d.getProducto().getId(),
                d.getProducto().getNombre(),
                d.getCantidad(),
                d.getPrecioUnitario(),
                (d.getCantidad() * d.getPrecioUnitario())
        );
    }
}