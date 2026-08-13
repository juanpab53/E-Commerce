package com.ecommerce.controller;

import com.ecommerce.dto.PaymentDTO;
import com.ecommerce.dto.PaymentResponseDTO;
import com.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> process(@Valid @RequestBody PaymentDTO paymentDto) {
        PaymentResponseDTO response = paymentService.processPayment(paymentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> findByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.findByOrderId(orderId));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> listAll() {
        return ResponseEntity.ok(paymentService.listAll());
    }
}
