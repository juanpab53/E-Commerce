package com.ecommerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.dto.OrderItemResponseDTO;
import com.ecommerce.dto.OrderResponseDTO;
import com.ecommerce.identity.domain.User;
import com.ecommerce.identity.domain.UserRepository;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    @Transactional
    public OrderResponseDTO createOrder(OrderDTO order) {
        User user = userRepository.findById(order.userId())
                .orElseThrow(() -> new NotFoundException(
                        "Cannot create the order: User not found with ID: " + order.userId()));

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (OrderItemDTO item : order.orderItems()) {
            Product productFromDB = productRepository.findById(item.productId())
                    .orElseThrow(() -> new NotFoundException(
                            "Detail error: Product not found with ID: " + item.productId()));

            if (productFromDB.getQuantity() < item.quantity()) {
                throw new BusinessRuleException("Insufficient stock for the product '" + productFromDB.getName() +
                        "'. Available: " + productFromDB.getQuantity() +
                        ", Requested: " + item.quantity());
            }

            productFromDB.setQuantity(productFromDB.getQuantity() - item.quantity());
            productRepository.save(productFromDB);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(newOrder);
            orderItem.setProduct(productFromDB);
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(productFromDB.getPrice());

            orderItems.add(orderItem);
            total += (productFromDB.getPrice() * item.quantity());
        }

        newOrder.setOrderItems(orderItems);
        newOrder.setTotal(total);

        Order savedOrder = orderRepository.save(newOrder);
        return toResponse(savedOrder);
    }

    @Transactional
    public List<OrderResponseDTO> listAll() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<OrderResponseDTO> listOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id));
        return toResponse(order);
    }

    @Transactional
    public OrderResponseDTO changeStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot change the status: Order not found with ID: " + orderId));

        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            refundStock(order);
        }

        order.setStatus(newStatus);
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cannot cancel: Order not found with ID: " + id));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessRuleException("Action not allowed: An order that has already been " + order.getStatus() +
                    " cannot be cancelled.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Notice: The order is already in CANCELLED status.");
        }

        refundStock(order);
        order.setStatus(OrderStatus.CANCELLED);

        return toResponse(orderRepository.save(order));
    }

    private void refundStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }
    }

    private OrderResponseDTO toResponse(Order order) {
        List<OrderItemResponseDTO> itemsDTO = order.getOrderItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        (item.getQuantity() * item.getUnitPrice())))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getOrderDate().toString(),
                order.getStatus().name(),
                order.getTotal(),
                itemsDTO);
    }
}
