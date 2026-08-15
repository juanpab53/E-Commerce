package com.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.ecommerce.dto.OrderItemResponseDTO;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.Product;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.shared.domain.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private OrderItem exampleOrderItem;
    private Product exampleProduct;

    @BeforeEach
    void setUp() {
        exampleProduct = new Product();
        exampleProduct.setId(100L);
        exampleProduct.setName("Mouse Gamer");
        exampleProduct.setPrice(50.0);

        exampleOrderItem = new OrderItem();
        exampleOrderItem.setId(1L);
        exampleOrderItem.setProduct(exampleProduct);
        exampleOrderItem.setQuantity(2);
        exampleOrderItem.setUnitPrice(50.0);
    }

    @Test
    @DisplayName("Should find an order item by ID successfully")
    void findByIdSuccess() {
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(exampleOrderItem));

        OrderItemResponseDTO result = orderItemService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Mouse Gamer", result.productName());
        assertEquals(100.0, result.subtotal());
    }

    @Test
    @DisplayName("Should throw an exception if the order item is not found by ID")
    void findByIdFails() {
        when(orderItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderItemService.findById(99L));
    }

    @Test
    @DisplayName("Should list order items by order ID")
    void listByOrderIdSuccess() {
        when(orderItemRepository.findByOrderId(10L)).thenReturn(List.of(exampleOrderItem));

        List<OrderItemResponseDTO> result = orderItemService.listByOrderId(10L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).subtotal());
    }
}
