package com.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import com.ecommerce.dto.CategoriaDTO;
import com.ecommerce.dto.CategoriaResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.Categoria;
import com.ecommerce.repository.CategoriaRepository;
import com.ecommerce.repository.ProductRepository;
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
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductRepository productoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoriaEjemplo;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoriaEjemplo = new Categoria();
        categoriaEjemplo.setId(1L);
        categoriaEjemplo.setNombre("Electrónica");

        categoriaDTO = new CategoriaDTO("Electrónica");
    }

    // --- PRUEBAS DE CREACIÓN ---

    @Test
    @DisplayName("Debe crear una categoría exitosamente")
    void crearCategoriaExito() {
        when(categoriaRepository.existsByNombre(anyString())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaEjemplo);

        CategoriaResponseDTO resultado = categoriaService.crear(categoriaDTO);

        assertNotNull(resultado);
        assertEquals("Electrónica", resultado.nombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Debe fallar al crear si el nombre ya existe")
    void crearCategoriaFallaDuplicado() {
        when(categoriaRepository.existsByNombre(categoriaDTO.nombre())).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> categoriaService.crear(categoriaDTO));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    // --- PRUEBAS DE BÚSQUEDA ---

    @Test
    @DisplayName("Debe listar todas las categorías")
    void listarTodasExito() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoriaEjemplo));

        List<CategoriaResponseDTO> resultado = categoriaService.listarTodas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Debe buscar categoría por ID exitosamente")
    void buscarPorIdExito() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));

        CategoriaResponseDTO resultado = categoriaService.buscarPorId(1L);

        assertEquals("Electrónica", resultado.nombre());
    }

    @Test
    @DisplayName("Debe lanzar excepción si no encuentra la categoría por ID")
    void buscarPorIdFalla() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoriaService.buscarPorId(99L));
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Debe actualizar categoría exitosamente")
    void actualizarCategoriaExito() {
        CategoriaDTO nuevoDto = new CategoriaDTO("Hogar");
        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setId(1L);
        categoriaActualizada.setNombre("Hogar");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));
        when(categoriaRepository.existsByNombre("Hogar")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaActualizada);

        CategoriaResponseDTO resultado = categoriaService.actualizar(1L, nuevoDto);

        assertEquals("Hogar", resultado.nombre());
    }

    @Test
    @DisplayName("Debe fallar actualización si el nuevo nombre ya existe")
    void actualizarCategoriaFallaNombreDuplicado() {
        CategoriaDTO nuevoDto = new CategoriaDTO("Hogar");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));
        when(categoriaRepository.existsByNombre("Hogar")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> categoriaService.actualizar(1L, nuevoDto));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    // --- PRUEBAS DE ELIMINACIÓN ---

    @Test
    @DisplayName("Debe eliminar categoría exitosamente")
    void eliminarCategoriaExito() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));
        when(productoRepository.existsByCategoriaId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> categoriaService.eliminar(1L));
        verify(categoriaRepository).delete(categoriaEjemplo);
    }

    @Test
    @DisplayName("Debe fallar eliminación si tiene productos asociados")
    void eliminarCategoriaFallaIntegridad() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEjemplo));
        when(productoRepository.existsByCategoriaId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> categoriaService.eliminar(1L));
        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }
}
