package com.ecommerce.service;

import com.ecommerce.dto.CategoriaDTO;
import com.ecommerce.dto.CategoriaResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.Categoria;
import com.ecommerce.repository.CategoriaRepository;
import com.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final ProductRepository productoRepository;

    @Transactional
    public CategoriaResponseDTO crear(CategoriaDTO dto) {
        if (categoriaRepository.existsByNombre(dto.nombre())) {
            throw new BusinessRuleException("Conflicto de datos: La categoría con el nombre '" + dto.nombre() + "' ya existe.");
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
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));
        return mapearAResponse(categoria);
    }

    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaDTO dto) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se puede actualizar: Categoría no encontrada con ID: " + id));
        
        if (!categoriaExistente.getNombre().equals(dto.nombre()) && categoriaRepository.existsByNombre(dto.nombre())) {
            throw new BusinessRuleException("No se puede actualizar: Ya existe otra categoría con el nombre '" + dto.nombre() + "'.");
        }

        categoriaExistente.setNombre(dto.nombre());
        Categoria actualizada = categoriaRepository.save(categoriaExistente);
        return mapearAResponse(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No se puede eliminar: Categoría no encontrada con ID: " + id));

        if (productoRepository.existsByCategoriaId(id)) {
            throw new BusinessRuleException("Restricción de integridad: No se puede eliminar la categoría '" + 
                                           categoria.getNombre() + "' porque tiene productos asociados.");
        }
        
        categoriaRepository.delete(categoria);
    }

    private CategoriaResponseDTO mapearAResponse(Categoria c) {
        return new CategoriaResponseDTO(c.getId(), c.getNombre());
    }
}