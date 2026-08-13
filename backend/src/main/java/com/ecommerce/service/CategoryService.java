package com.ecommerce.service;

import com.ecommerce.dto.CategoryDTO;
import com.ecommerce.dto.CategoryResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    @Transactional
    public CategoryResponseDTO create(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.name())) {
            throw new BusinessRuleException("Data conflict: A category with the name '" + dto.name() + "' already exists.");
        }
        Category category = new Category();
        category.setName(dto.name());
        
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional
    public List<CategoryResponseDTO> listAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponseDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));
        return toResponse(category);
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryDTO dto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cannot update: Category not found with ID: " + id));
        
        if (!existingCategory.getName().equals(dto.name()) && categoryRepository.existsByName(dto.name())) {
            throw new BusinessRuleException("Cannot update: Another category already exists with the name '" + dto.name() + "'.");
        }

        existingCategory.setName(dto.name());
        Category updated = categoryRepository.save(existingCategory);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cannot delete: Category not found with ID: " + id));

        if (productRepository.existsByCategoryId(id)) {
            throw new BusinessRuleException("Integrity constraint: The category '" + 
                                           category.getName() + "' cannot be deleted because it has associated products.");
        }
        
        categoryRepository.delete(category);
    }

    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
