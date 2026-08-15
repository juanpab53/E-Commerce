package com.ecommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.OrderItemResponseDTO;
import com.ecommerce.model.OrderItem;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.shared.domain.NotFoundException;

@RequiredArgsConstructor
@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderItemResponseDTO findById(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order item not found with ID: " + id));
        return toResponse(orderItem);
    }

    @Transactional
    public List<OrderItemResponseDTO> listByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private OrderItemResponseDTO toResponse(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                (item.getQuantity() * item.getUnitPrice())
        );
    }
}
