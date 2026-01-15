package com.Ecommerce.PruebaE_Commerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.Ecommerce.PruebaE_Commerce.dto.PagoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PagoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.exceptions.BusinessLogicException;
import com.Ecommerce.PruebaE_Commerce.exceptions.ResourceNotFoundException;
import com.Ecommerce.PruebaE_Commerce.model.Estado;
import com.Ecommerce.PruebaE_Commerce.model.MetodoPago;
import com.Ecommerce.PruebaE_Commerce.model.Pago;
import com.Ecommerce.PruebaE_Commerce.model.Pedido;
import com.Ecommerce.PruebaE_Commerce.repository.PagoRepository;
import com.Ecommerce.PruebaE_Commerce.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PagoService pagoService;

    private Pedido pedidoEjemplo;
    private Pago pagoEjemplo;
    private PagoDTO pagoDTO;

    @BeforeEach
    void setUp() {
        pedidoEjemplo = new Pedido();
        pedidoEjemplo.setId(1L);
        pedidoEjemplo.setTotal(1500.0);
        pedidoEjemplo.setEstado(Estado.PENDIENTE);

        pagoEjemplo = new Pago();
        pagoEjemplo.setId(10L);
        pagoEjemplo.setPedido(pedidoEjemplo);
        pagoEjemplo.setMonto(1500.0);
        pagoEjemplo.setFechaPago(LocalDateTime.now().toString());
        pagoEjemplo.setMetodoPago(MetodoPago.TARJETA_CREDITO);

        pagoDTO = new PagoDTO(1L, MetodoPago.TARJETA_CREDITO);
    }

    // --- PRUEBAS DE PROCESAR PAGO ---

    @Test
    @DisplayName("Debe procesar el pago exitosamente y actualizar estado del pedido")
    void procesarPagoExito() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));
        when(pagoRepository.findByPedidoId(1L)).thenReturn(Optional.empty());
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoEjemplo);
        
        PagoResponseDTO resultado = pagoService.processarPago(pagoDTO);

        assertNotNull(resultado);
        // Verificamos que el estado del pedido en memoria haya cambiado
        assertEquals(Estado.PAGADO, pedidoEjemplo.getEstado());
        // Verificamos que se guarden ambos cambios
        verify(pedidoRepository).save(pedidoEjemplo);
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe fallar si el pedido no existe")
    void procesarPagoFallaPedidoNoEncontrado() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pagoService.processarPago(pagoDTO));
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe fallar si el pedido ya está pagado")
    void procesarPagoFallaYaPagado() {
        pedidoEjemplo.setEstado(Estado.PAGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));

        assertThrows(BusinessLogicException.class, () -> pagoService.processarPago(pagoDTO));
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe fallar si el pedido está cancelado")
    void procesarPagoFallaCancelado() {
        pedidoEjemplo.setEstado(Estado.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));

        assertThrows(BusinessLogicException.class, () -> pagoService.processarPago(pagoDTO));
    }

    @Test
    @DisplayName("Debe fallar si ya existe un registro de pago para el pedido")
    void procesarPagoFallaDuplicado() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEjemplo));
        when(pagoRepository.findByPedidoId(1L)).thenReturn(Optional.of(pagoEjemplo));

        assertThrows(BusinessLogicException.class, () -> pagoService.processarPago(pagoDTO));
    }

    // --- PRUEBAS DE BÚSQUEDA ---

    @Test
    @DisplayName("Debe buscar pago por ID")
    void buscarPorIdExito() {
        when(pagoRepository.findById(10L)).thenReturn(Optional.of(pagoEjemplo));

        PagoResponseDTO resultado = pagoService.buscarPorId(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
    }

    @Test
    @DisplayName("Debe buscar pago por ID de pedido")
    void buscarPorPedidoExito() {
        when(pagoRepository.findByPedidoId(1L)).thenReturn(Optional.of(pagoEjemplo));

        PagoResponseDTO resultado = pagoService.buscarPorPedido(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.pedidoId());
    }
}
