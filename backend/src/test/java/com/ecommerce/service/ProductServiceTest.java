package com.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.OrderItemRepository;
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
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ProductService productService;

    private Product exampleProduct;
    private Category exampleCategory;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        exampleCategory = new Category();
        exampleCategory.setId(1L);
        exampleCategory.setName("Electrónica");

        exampleProduct = new Product();
        exampleProduct.setId(100L);
        exampleProduct.setName("Laptop Gamer");
        exampleProduct.setDescription("Potente laptop");
        exampleProduct.setPrice(2500.0);
        exampleProduct.setQuantity(10);
        exampleProduct.setCategory(exampleCategory);

        productDTO = new ProductDTO(
                "Laptop Gamer",
                "Potente laptop",
                2500.0,
                10,
                1L
        );
    }

    // --- CREATION TESTS ---

    @Test
    @DisplayName("Should create a product successfully")
    void createProductSuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(exampleCategory));
        when(productRepository.save(any(Product.class))).thenReturn(exampleProduct);

        ProductResponseDTO result = productService.create(productDTO);

        assertNotNull(result);
        assertEquals("Laptop Gamer", result.name());
        assertEquals("Electrónica", result.categoryName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should fail creation if the category does not exist")
    void createProductFailsCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.create(productDTO));
        verify(productRepository, never()).save(any(Product.class));
    }

    // --- SEARCH TESTS ---

    @Test
    @DisplayName("Should list all products")
    void listAllSuccess() {
        when(productRepository.findAll()).thenReturn(List.of(exampleProduct));

        List<ProductResponseDTO> result = productService.listAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should find a product by ID successfully")
    void findByIdSuccess() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(exampleProduct));

        ProductResponseDTO result = productService.findById(100L);

        assertEquals(exampleProduct.getName(), result.name());
    }

    @Test
    @DisplayName("Should throw an exception if the product is not found by ID")
    void findByIdFails() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.findById(999L));
    }

    @Test
    @DisplayName("Should find products by name")
    void findByNameSuccess() {
        when(productRepository.findByNameContainingIgnoreCase("Laptop")).thenReturn(List.of(exampleProduct));

        List<ProductResponseDTO> result = productService.findByName("Laptop");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should list products by category")
    void listByCategorySuccess() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(exampleProduct));

        List<ProductResponseDTO> result = productService.listByCategory(1L);

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should fail to list by category if it does not exist")
    void listByCategoryFailsNotFound() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> productService.listByCategory(99L));
    }

    // --- STOCK UPDATE TESTS ---

    @Test
    @DisplayName("Should update stock correctly")
    void updateStockSuccess() {
        when(productRepository.findById(100L)).thenReturn(Optional.of(exampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(exampleProduct);

        ProductResponseDTO result = productService.updateStock(100L, 50);

        assertNotNull(result);
        assertEquals(50, exampleProduct.getQuantity());
    }

    @Test
    @DisplayName("Should fail stock update if it is negative")
    void updateStockFailsNegative() {
        assertThrows(BusinessRuleException.class, () -> productService.updateStock(100L, -5));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should fail stock update if the product does not exist")
    void updateStockFailsNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.updateStock(999L, 10));
    }

    // --- DELETION TESTS ---

    @Test
    @DisplayName("Should delete a product successfully")
    void deleteProductSuccess() {
        when(productRepository.existsById(100L)).thenReturn(true);
        when(orderItemRepository.existsByProductId(100L)).thenReturn(false);

        assertDoesNotThrow(() -> productService.delete(100L));
        verify(productRepository).deleteById(100L);
    }

    @Test
    @DisplayName("Should fail deletion if the product does not exist")
    void deleteProductFailsNotFound() {
        when(productRepository.existsById(999L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> productService.delete(999L));
    }

    @Test
    @DisplayName("Should fail deletion if it has sales history (Integrity)")
    void deleteProductFailsIntegrity() {
        when(productRepository.existsById(100L)).thenReturn(true);
        when(orderItemRepository.existsByProductId(100L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> productService.delete(100L));
        verify(productRepository, never()).deleteById(anyLong());
    }
}
