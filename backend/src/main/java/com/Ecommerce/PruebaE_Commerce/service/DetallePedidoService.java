package com.Ecommerce.PruebaE_Commerce.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.dto.DetallePedidoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.exceptions.ResourceNotFoundException;
import com.Ecommerce.PruebaE_Commerce.model.DetallePedido;
import com.Ecommerce.PruebaE_Commerce.repository.DetallePedidoRepository;
import jakarta.transaction.Transactional;

@Service
public class DetallePedidoService {
    @Autowired
    private DetallePedidoRepository detalleRepository;

    @Transactional
    public DetallePedidoResponseDTO buscarPorId(Long id) {
        DetallePedido detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado con ID: " + id));
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