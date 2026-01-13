package com.Ecommerce.PruebaE_Commerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.dto.DetallePedidoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.DetallePedidoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PedidoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PedidoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.exceptions.BusinessLogicException;
import com.Ecommerce.PruebaE_Commerce.exceptions.ResourceNotFoundException;
import com.Ecommerce.PruebaE_Commerce.model.DetallePedido;
import com.Ecommerce.PruebaE_Commerce.model.Estado;
import com.Ecommerce.PruebaE_Commerce.model.Pedido;
import com.Ecommerce.PruebaE_Commerce.model.Producto;
import com.Ecommerce.PruebaE_Commerce.model.Usuario;
import com.Ecommerce.PruebaE_Commerce.repository.PedidoRepository;
import com.Ecommerce.PruebaE_Commerce.repository.ProductRepository;
import com.Ecommerce.PruebaE_Commerce.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public PedidoResponseDTO crearPedido(PedidoDTO pedido) {
        // RECURSO: Validar usuario
        Usuario usuario = userRepository.findById(pedido.usuarioID())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear el pedido: Usuario no encontrado con ID: " + pedido.usuarioID()));

        Pedido pedidoNuevo = new Pedido();
        pedidoNuevo.setUsuario(usuario);
        pedidoNuevo.setFechaPedido(LocalDateTime.now().toString());
        pedidoNuevo.setEstado(Estado.PENDIENTE);

        List<DetallePedido> detalles = new ArrayList<>();
        double total = 0;

        for (DetallePedidoDTO detalle : pedido.detalles()) {
            // RECURSO: Validar existencia del producto
            Producto productoDB = productRepository.findById(detalle.productoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Error en el detalle: Producto no encontrado con ID: " + detalle.productoId()));

            // NEGOCIO: Validar disponibilidad de stock
            if (productoDB.getCantidad() < detalle.cantidad()) {
                throw new BusinessLogicException("Stock insuficiente para el producto '" + productoDB.getNombre() + 
                                               "'. Disponible: " + productoDB.getCantidad() + ", Solicitado: " + detalle.cantidad());
            }

            // Descontar stock
            productoDB.setCantidad(productoDB.getCantidad() - detalle.cantidad());
            productRepository.save(productoDB);

            DetallePedido detallePedido = new DetallePedido();
            detallePedido.setPedido(pedidoNuevo);
            detallePedido.setProducto(productoDB);
            detallePedido.setCantidad(detalle.cantidad());
            detallePedido.setPrecioUnitario(productoDB.getPrecio());

            detalles.add(detallePedido);
            total += (productoDB.getPrecio() * detalle.cantidad());
        }

        pedidoNuevo.setDetallePedidos(detalles);
        pedidoNuevo.setTotal(total);

        Pedido pedidoGuardado = pedidoRepository.save(pedidoNuevo);
        return mapearAResponse(pedidoGuardado);
    }

    @Transactional
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PedidoResponseDTO> listarPedidosPorUsuario(Long usuarioId) {
        // Opcional: Podrías validar si el usuario existe antes de listar
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        return mapearAResponse(pedido);
    }

    @Transactional
    public PedidoResponseDTO cambiarEstado(Long pedidoId, Estado nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede cambiar el estado: Pedido no encontrado con ID: " + pedidoId));

        // NEGOCIO: Evitar cambios si ya está cancelado o si se requiere lógica de reintegro
        if (nuevoEstado == Estado.CANCELADO && pedido.getEstado() != Estado.CANCELADO) {
            devolverStock(pedido);
        }
        
        pedido.setEstado(nuevoEstado);
        return mapearAResponse(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponseDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede cancelar: Pedido no encontrado con ID: " + id));

        // NEGOCIO: Validar transiciones de estado permitidas
        if (pedido.getEstado() == Estado.ENVIADO || pedido.getEstado() == Estado.ENTREGADO) {
            throw new BusinessLogicException("Acción no permitida: No se puede cancelar un pedido que ya ha sido " + pedido.getEstado());
        }

        if (pedido.getEstado() == Estado.CANCELADO) {
            throw new BusinessLogicException("Aviso: El pedido ya se encuentra en estado CANCELADO.");
        }
        
        devolverStock(pedido);
        pedido.setEstado(Estado.CANCELADO);
        
        return mapearAResponse(pedidoRepository.save(pedido));
    }

    private void devolverStock(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetallePedidos()) {
            Producto producto = detalle.getProducto();
            producto.setCantidad(producto.getCantidad() + detalle.getCantidad());
            productRepository.save(producto);
        }
    }

    private PedidoResponseDTO mapearAResponse(Pedido p) {
        List<DetallePedidoResponseDTO> detallesDTO = p.getDetallePedidos().stream()
                .map(d -> new DetallePedidoResponseDTO(
                        d.getId(),
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        (d.getCantidad() * d.getPrecioUnitario())))
                .toList();

        return new PedidoResponseDTO(
                p.getId(),
                p.getFechaPedido(),
                p.getEstado().name(),
                p.getTotal(),
                detallesDTO);
    }
}