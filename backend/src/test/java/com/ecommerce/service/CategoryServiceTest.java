package com.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecommerce.dto.CategoryDTO;
import com.ecommerce.dto.CategoryResponseDTO;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category exampleCategory;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        exampleCategory = new Category();
        exampleCategory.setId(1L);
        exampleCategory.setName("Electrónica");

        categoryDTO = new CategoryDTO("Electrónica");
    }

    // --- CREATION TESTS ---

    @Test
    @DisplayName("Should create a category successfully")
    void createCategorySuccess() {
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(exampleCategory);

        CategoryResponseDTO result = categoryService.create(categoryDTO);

        assertNotNull(result);
        assertEquals("Electrónica", result.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should fail creation if the name already exists")
    void createCategoryFailsDuplicate() {
        when(categoryRepository.existsByName(categoryDTO.name())).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> categoryService.create(categoryDTO));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // --- SEARCH TESTS ---

    @Test
    @DisplayName("Should list all categories")
    void listAllSuccess() {
        when(categoryRepository.findAll()).thenReturn(List.of(exampleCategory));

        List<CategoryResponseDTO> result = categoryService.listAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should find a category by ID successfully")
    void findByIdSuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(exampleCategory));

        CategoryResponseDTO result = categoryService.findById(1L);

        assertEquals("Electrónica", result.name());
    }

    @Test
    @DisplayName("Should throw an exception if the category is not found by ID")
    void findByIdFails() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.findById(99L));
    }

    // --- UPDATE TESTS ---

    @Test
    @DisplayName("Should update a category successfully")
    void updateCategorySuccess() {
        CategoryDTO newDto = new CategoryDTO("Hogar");
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Hogar");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(exampleCategory));
        when(categoryRepository.existsByName("Hogar")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryResponseDTO result = categoryService.update(1L, newDto);

        assertEquals("Hogar", result.name());
    }

    @Test
    @DisplayName("Should fail update if the new name already exists")
    void updateCategoryFailsDuplicateName() {
        CategoryDTO newDto = new CategoryDTO("Hogar");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(exampleCategory));
        when(categoryRepository.existsByName("Hogar")).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> categoryService.update(1L, newDto));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // --- DELETION TESTS ---

    @Test
    @DisplayName("Should delete a category successfully")
    void deleteCategorySuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(exampleCategory));
        when(productRepository.existsByCategoryId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> categoryService.delete(1L));
        verify(categoryRepository).delete(exampleCategory);
    }

    @Test
    @DisplayName("Should fail deletion if it has associated products")
    void deleteCategoryFailsIntegrity() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(exampleCategory));
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> categoryService.delete(1L));
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
