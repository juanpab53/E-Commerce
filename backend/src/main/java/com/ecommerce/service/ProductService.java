package com.ecommerce.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final OrderItemRepository orderItemRepository;

    @Transactional
    public ProductResponseDTO create(ProductDTO productDto) {
        Category category = categoryRepository.findById(productDto.categoryId())
                .orElseThrow(() -> new NotFoundException("Cannot create product: Category not found with ID: " + productDto.categoryId()));
        
        Product product = new Product();
        product.setName(productDto.name());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        product.setQuantity(productDto.quantity());
        product.setCategory(category);

        return toResponse(productRepository.save(product));
    }

    @Transactional
    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
    }

    @Transactional
    public List<ProductResponseDTO> listAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
        return toResponse(product);
    }

    @Transactional
    public List<ProductResponseDTO> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public List<ProductResponseDTO> listByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Cannot list products: Category not found with ID: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponseDTO updateStock(Long id, Integer newStock) {
        if (newStock < 0) {
            throw new BusinessRuleException("Inventory error: Stock cannot be negative (" + newStock + ")");
        }
        Product product = findProduct(id);
        product.setQuantity(newStock);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Cannot delete: Product not found with ID: " + id);
        }

        if (orderItemRepository.existsByProductId(id)) {
            throw new BusinessRuleException("Integrity constraint: The product with ID " + id + 
                                           " has sales history and cannot be physically deleted.");
        }

        productRepository.deleteById(id);
    }

    private ProductResponseDTO toResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getQuantity(), product.getCategory().getName());
    }
}
