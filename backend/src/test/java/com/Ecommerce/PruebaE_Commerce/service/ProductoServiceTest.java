package com.Ecommerce.PruebaE_Commerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import com.Ecommerce.PruebaE_Commerce.dto.ProductoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.ProductoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.exceptions.BusinessLogicException;
import com.Ecommerce.PruebaE_Commerce.exceptions.ResourceNotFoundException;
import com.Ecommerce.PruebaE_Commerce.model.Categoria;
import com.Ecommerce.PruebaE_Commerce.model.Producto;
import com.Ecommerce.PruebaE_Commerce.repository.CategoriaRepository;
import com.Ecommerce.PruebaE_Commerce.repository.DetallePedidoRepository;
import com.Ecommerce.PruebaE_Commerce.repository.ProductRepository;
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
public class ProductoServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoEjemplo;
    private Categoria categoriaEjemplo;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        categoriaEjemplo = new Categoria();
        categoriaEjemplo.setId(1L);
        categoriaEjemplo.setNombre("Electrónica");

        productoEjemplo = new Producto();
        productoEjemplo.setId(100L);
        productoEjemplo.setNombre("Laptop Gamer");
        productoEjemplo.setDescripcion("Potente laptop");
        productoEjemplo.setPrecio(2500.0);
        productoEjemplo.setCantidad(10);
        productoEjemplo.setCategoria(categoriaEjemplo);

        productoDTO = new ProductoDTO(
                "Laptop Gamer",
                "Potente laptop",
                2500.0,
                10,
                1L
        );
    }

    // --- PRUEBAS DE CREACIÓN ---

    @Test
    @DisplayName("Debe crear un producto exitosamente")
    void crearProductoExito() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));
        when(productRepository.save(any(Producto.class))).thenReturn(productoEjemplo);

        ProductoResponseDTO resultado = productoService.crear(productoDTO);

        assertNotNull(resultado);
        assertEquals("Laptop Gamer", resultado.nombre());
        assertEquals("Electrónica", resultado.nombreCategoria());
        verify(productRepository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe fallar al crear si la categoría no existe")
    void crearProductoFallaCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.crear(productoDTO));
        verify(productRepository, never()).save(any(Producto.class));
    }

    // --- PRUEBAS DE BÚSQUEDA ---

    @Test
    @DisplayName("Debe listar todos los productos")
    void listarTodosExito() {
        when(productRepository.findAll()).thenReturn(List.of(productoEjemplo));

        List<ProductoResponseDTO> resultado = productoService.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Debe buscar producto por ID exitosamente")
    void buscarPorIdExito() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(productoEjemplo));

        ProductoResponseDTO resultado = productoService.buscarPorId(100L);

        assertEquals(productoEjemplo.getNombre(), resultado.nombre());
    }

    @Test
    @DisplayName("Debe lanzar excepción si no encuentra el producto por ID")
    void buscarPorIdFalla() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Debe listar productos por categoría")
    void listarPorCategoriaExito() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findByCategoriaId(1L)).thenReturn(List.of(productoEjemplo));

        List<ProductoResponseDTO> resultado = productoService.listarPorCategoria(1L);

        assertFalse(resultado.isEmpty());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN DE STOCK ---

    @Test
    @DisplayName("Debe actualizar stock correctamente")
    void actualizarStockExito() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(productoEjemplo));
        when(productRepository.save(any(Producto.class))).thenReturn(productoEjemplo);

        ProductoResponseDTO resultado = productoService.actualizarStock(100L, 50);

        assertNotNull(resultado);
        assertEquals(50, productoEjemplo.getCantidad());
    }

    // --- PRUEBAS DE ELIMINACIÓN ---

    @Test
    @DisplayName("Debe fallar eliminación si tiene historial de ventas (Integridad)")
    void eliminarProductoFallaIntegridad() {
        when(productRepository.existsById(100L)).thenReturn(true);
        when(detallePedidoRepository.existsByProductoId(100L)).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> productoService.eliminar(100L));
        verify(productRepository, never()).deleteById(anyLong());
    }
}
