package com.Ecommerce.PruebaE_Commerce.service;

import com.Ecommerce.PruebaE_Commerce.dto.CategoriaDTO;
import com.Ecommerce.PruebaE_Commerce.dto.CategoriaResponseDTO;
import com.Ecommerce.PruebaE_Commerce.exceptions.BusinessLogicException;
import com.Ecommerce.PruebaE_Commerce.exceptions.ResourceNotFoundException;
import com.Ecommerce.PruebaE_Commerce.model.Categoria;
import com.Ecommerce.PruebaE_Commerce.repository.CategoriaRepository;
import com.Ecommerce.PruebaE_Commerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductRepository productoRepository;

    @Transactional
    public CategoriaResponseDTO crear(CategoriaDTO dto) {
        // NEGOCIO: Evitar nombres duplicados
        if (categoriaRepository.existsByNombre(dto.nombre())) {
            throw new BusinessLogicException("Conflicto de datos: La categoría con el nombre '" + dto.nombre() + "' ya existe.");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());
        
        Categoria guardada = categoriaRepository.save(categoria);
        return mapearAResponse(guardada);
    }

    @Transactional
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoriaResponseDTO buscarPorId(Long id) {
        // RECURSO: Not Found (404)
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return mapearAResponse(categoria);
    }

    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaDTO dto) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Categoría no encontrada con ID: " + id));
        
        // Opcional: Validar que el nuevo nombre no choque con otra categoría existente
        if (!categoriaExistente.getNombre().equals(dto.nombre()) && categoriaRepository.existsByNombre(dto.nombre())) {
            throw new BusinessLogicException("No se puede actualizar: Ya existe otra categoría con el nombre '" + dto.nombre() + "'.");
        }

        categoriaExistente.setNombre(dto.nombre());
        Categoria actualizada = categoriaRepository.save(categoriaExistente);
        return mapearAResponse(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        // RECURSO: Validar existencia antes de intentar cualquier lógica
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Categoría no encontrada con ID: " + id));

        // NEGOCIO: Integridad referencial (No dejar productos huérfanos)
        if (productoRepository.existsByCategoriaId(id)) {
            throw new BusinessLogicException("Restricción de integridad: No se puede eliminar la categoría '" + 
                                           categoria.getNombre() + "' porque tiene productos asociados.");
        }
        
        categoriaRepository.delete(categoria);
    }

    private CategoriaResponseDTO mapearAResponse(Categoria c) {
        return new CategoriaResponseDTO(c.getId(), c.getNombre());
    }
}