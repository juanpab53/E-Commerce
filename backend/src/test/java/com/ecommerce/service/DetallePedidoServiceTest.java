package com.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecommerce.dto.DetallePedidoResponseDTO;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.DetallePedido;
import com.ecommerce.model.Producto;
import com.ecommerce.repository.DetallePedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DetallePedidoServiceTest {

    @Mock
    private DetallePedidoRepository detalleRepository;

    @InjectMocks
    private DetallePedidoService detalleService;

    private DetallePedido detalleEjemplo;
    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        productoEjemplo = new Producto();
        productoEjemplo.setId(100L);
        productoEjemplo.setNombre("Mouse Gamer");
        productoEjemplo.setPrecio(50.0);

        detalleEjemplo = new DetallePedido();
        detalleEjemplo.setId(1L);
        detalleEjemplo.setProducto(productoEjemplo);
        detalleEjemplo.setCantidad(2);
        detalleEjemplo.setPrecioUnitario(50.0);
    }

    @Test
    @DisplayName("Debe buscar detalle por ID exitosamente")
    void buscarPorIdExito() {
        when(detalleRepository.findById(1L)).thenReturn(Optional.of(detalleEjemplo));

        DetallePedidoResponseDTO resultado = detalleService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Mouse Gamer", resultado.nombreProducto());
        assertEquals(100.0, resultado.subtotal()); 
    }

    @Test
    @DisplayName("Debe lanzar excepción si no encuentra el detalle por ID")
    void buscarPorIdFalla() {
        when(detalleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> detalleService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Debe listar detalles por ID de pedido")
    void listarPorPedidoExito() {
        when(detalleRepository.findByPedidoId(10L)).thenReturn(List.of(detalleEjemplo));

        List<DetallePedidoResponseDTO> resultado = detalleService.listarPorPedido(10L);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(100.0, resultado.get(0).subtotal());
    }
}
