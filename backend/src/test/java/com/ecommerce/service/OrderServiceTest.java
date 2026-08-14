package com.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.dto.OrderResponseDTO;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private User exampleUser;
    private Product exampleProduct;
    private Order exampleOrder;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        exampleUser = new User();
        exampleUser.setId(1L);
        exampleUser.setName("Juan");
        exampleUser.setEmail("juan@example.com");

        exampleProduct = new Product();
        exampleProduct.setId(100L);
        exampleProduct.setName("Laptop");
        exampleProduct.setPrice(1000.0);
        exampleProduct.setQuantity(10);

        exampleOrder = new Order();
        exampleOrder.setId(1L);
        exampleOrder.setUser(exampleUser);
        exampleOrder.setOrderDate(LocalDateTime.now());
        exampleOrder.setStatus(OrderStatus.PENDING);
        exampleOrder.setTotal(2000.0);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(exampleProduct);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(1000.0);
        orderItem.setOrder(exampleOrder);

        exampleOrder.setOrderItems(List.of(orderItem));

        OrderItemDTO orderItemDTO = new OrderItemDTO(100L, 2);
        orderDTO = new OrderDTO(1L, List.of(orderItemDTO));
    }

    // --- CREATION TESTS ---

    @Test
    @DisplayName("Should create an order successfully and discount stock")
    void createOrderSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        when(productRepository.findById(100L)).thenReturn(Optional.of(exampleProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(exampleOrder);

        OrderResponseDTO result = orderService.createOrder(orderDTO);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING.name(), result.status());
        assertEquals(2000.0, result.total());

        assertEquals(8, exampleProduct.getQuantity());
        verify(productRepository).save(exampleProduct);
    }

    @Test
    @DisplayName("Should fail if stock is insufficient")
    void createOrderFailsStock() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));
        when(productRepository.findById(100L)).thenReturn(Optional.of(exampleProduct));

        OrderItemDTO excessiveItem = new OrderItemDTO(100L, 20);
        OrderDTO excessiveOrder = new OrderDTO(1L, List.of(excessiveItem));

        assertThrows(BusinessRuleException.class, () -> orderService.createOrder(excessiveOrder));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // --- SEARCH TESTS ---

    @Test
    @DisplayName("Should find an order by ID")
    void findByIdSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));

        OrderResponseDTO result = orderService.findById(1L);

        assertEquals(1L, result.id());
    }

    // --- CANCELLATION AND STOCK REFUND TESTS ---

    @Test
    @DisplayName("Should cancel an order and refund stock")
    void cancelOrderSuccess() {
        exampleProduct.setQuantity(8);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponseDTO result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED.name(), result.status());
        assertEquals(10, exampleProduct.getQuantity());
        verify(productRepository).save(exampleProduct);
    }

    @Test
    @DisplayName("Should fail cancellation if the order was already shipped")
    void cancelOrderFailsShipped() {
        exampleOrder.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));

        assertThrows(BusinessRuleException.class, () -> orderService.cancelOrder(1L));
    }

    // --- LISTING TESTS ---

    @Test
    @DisplayName("Should list all orders")
    void listAllSuccess() {
        when(orderRepository.findAll()).thenReturn(List.of(exampleOrder));

        List<OrderResponseDTO> result = orderService.listAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should list orders by user")
    void listOrdersByUserSuccess() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(exampleOrder));

        List<OrderResponseDTO> result = orderService.listOrdersByUser(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    // --- STATUS CHANGE TESTS ---

    @Test
    @DisplayName("Should change status successfully")
    void changeStatusSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(exampleOrder);

        OrderResponseDTO result = orderService.changeStatus(1L, OrderStatus.SHIPPED);

        assertEquals(OrderStatus.SHIPPED.name(), result.status());
        verify(orderRepository).save(exampleOrder);
    }

    @Test
    @DisplayName("Should refund stock if the new status is CANCELLED via changeStatus")
    void changeStatusToCancelledRefundsStock() {
        exampleProduct.setQuantity(8);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(exampleOrder);

        OrderResponseDTO result = orderService.changeStatus(1L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED.name(), result.status());
        assertEquals(10, exampleProduct.getQuantity());
        verify(productRepository).save(exampleProduct);
    }

    @Test
    @DisplayName("Should fail status change if the order does not exist")
    void changeStatusFailsNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.changeStatus(99L, OrderStatus.SHIPPED));
    }
}
