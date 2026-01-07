package com.Ecommerce.PruebaE_Commerce.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Ecommerce.PruebaE_Commerce.model.DetallePedido;
import com.Ecommerce.PruebaE_Commerce.model.Estado;
import com.Ecommerce.PruebaE_Commerce.model.Pedido;
import com.Ecommerce.PruebaE_Commerce.model.Producto;
import com.Ecommerce.PruebaE_Commerce.repository.PedidoRepository;
import com.Ecommerce.PruebaE_Commerce.repository.ProductRepository;
import jakarta.transaction.Transactional;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        double total = 0;

        for (DetallePedido detalle : pedido.getDetallePedidos()) {
            Producto productoDB = productRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (productoDB.getCantidad() < detalle.getCantidad()) {
                throw new RuntimeException("No hay suficiente stock del producto: " + productoDB.getNombre());
            }

            productoDB.setCantidad(productoDB.getCantidad() - detalle.getCantidad());
            productRepository.save(productoDB);

            detalle.setPrecioUnitario(productoDB.getPrecio());
            total += (detalle.getPrecioUnitario() * detalle.getCantidad());

            detalle.setPedido(pedido);
        }

        pedido.setTotal(total);
        pedido.setFechaPedido(LocalDate.now().toString());
        pedido.setEstado(Estado.PENDIENTE);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cambiarEstado(Long pedidoId, Estado nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public Pedido actualizarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        for (DetallePedido detalle : pedido.getDetallePedidos()) {
            Producto producto = detalle.getProducto();
            producto.setCantidad(producto.getCantidad() + detalle.getCantidad());
            productRepository.save(producto);
        }

        pedido.setEstado(Estado.CANCELADO);
        pedidoRepository.save(pedido);
    }

}
