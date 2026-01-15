package com.Ecommerce.PruebaE_Commerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.Ecommerce.PruebaE_Commerce.dto.DetallePedidoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PedidoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PedidoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.exceptions.BusinessLogicException;
import com.Ecommerce.PruebaE_Commerce.model.*;
import com.Ecommerce.PruebaE_Commerce.repository.PedidoRepository;
import com.Ecommerce.PruebaE_Commerce.repository.ProductRepository;
import com.Ecommerce.PruebaE_Commerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuarioEjemplo;
    private Producto productoEjemplo;
    private Pedido pedidoEjemplo;
    private PedidoDTO pedidoDTO;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setNombre("Juan");
        usuarioEjemplo.setEmail("juan@example.com");

        productoEjemplo = new Producto();
        productoEjemplo.setId(100L);
        productoEjemplo.setNombre("Laptop");
        productoEjemplo.setPrecio(1000.0);
        productoEjemplo.setCantidad(10);

        pedidoEjemplo = new Pedido();
        pedidoEjemplo.setId(1L);
        pedidoEjemplo.setUsuario(usuarioEjemplo);
        pedidoEjemplo.setFechaPedido(LocalDateTime.now().toString());
        pedidoEjemplo.setEstado(Estado.PENDIENTE);
        pedidoEjemplo.setTotal(2000.0);

        DetallePedido detalle = new DetallePedido();
        detalle.setId(1L);
        detalle.setProducto(productoEjemplo);
        detalle.setCantidad(2); 
        detalle.setPrecioUnitario(1000.0);
        detalle.setPedido(pedidoEjemplo);
        
        pedidoEjemplo.setDetallePedidos(List.of(detalle));

        DetallePedidoDTO detalleDTO = new DetallePedidoDTO(100L, 2);
        pedidoDTO = new PedidoDTO(1L, List.of(detalleDTO));
    }

    // --- PRUEBAS DE CREACIÓN ---

    @Test
    @DisplayName("Debe crear un pedido exitosamente y descontar stock")
    void crearPedidoExito() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(productRepository.findById(100L)).thenReturn(Optional.of(productoEjemplo));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoEjemplo);

        PedidoResponseDTO resultado = pedidoService.crearPedido(pedidoDTO);

        assertNotNull(resultado);
        assertEquals(Estado.PENDIENTE.name(), resultado.estado());
        assertEquals(2000.0, resultado.total());
        
        assertEquals(8, productoEjemplo.getCantidad());
        verify(productRepository).save(productoEjemplo);
    }

    @Test
    @DisplayName("Debe fallar si hay stock insuficiente")
    void crearPedidoFallaStock() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(productRepository.findById(100L)).thenReturn(Optional.of(productoEjemplo));

        DetallePedidoDTO detalleExcesivo = new DetallePedidoDTO(100L, 20);
        PedidoDTO pedidoExcesivo = new PedidoDTO(1L, List.of(detalleExcesivo));

        assertThrows(BusinessLogicException.class, () -> pedidoService.crearPedido(pedidoExcesivo));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    // --- PRUEBAS DE BÚSQUEDA ---

    @Test
    @DisplayName("Debe buscar pedido por ID")
    void buscarPorIdExito() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));
        
        PedidoResponseDTO resultado = pedidoService.buscarPorId(1L);
        
        assertEquals(1L, resultado.id());
    }

    // --- PRUEBAS DE CANCELACIÓN Y DEVOLUCIÓN DE STOCK ---

    @Test
    @DisplayName("Debe cancelar pedido y devolver stock")
    void cancelarPedidoExito() {
        productoEjemplo.setCantidad(8);
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L);

        assertEquals(Estado.CANCELADO.name(), resultado.estado());
        assertEquals(10, productoEjemplo.getCantidad());
        verify(productRepository).save(productoEjemplo);
    }

    @Test
    @DisplayName("Debe fallar cancelación si el pedido ya fue enviado")
    void cancelarPedidoFallaEnviado() {
        pedidoEjemplo.setEstado(Estado.ENVIADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));

        assertThrows(BusinessLogicException.class, () -> pedidoService.cancelarPedido(1L));
    }
}
