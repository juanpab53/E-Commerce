package com.ecommerce.shared.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DomainExceptionTest {

    @Test
    @DisplayName("BusinessRuleException is a DomainException and preserves the message")
    void businessRuleExceptionHierarchy() {
        BusinessRuleException ex = new BusinessRuleException("Invalid operation");
        assertTrue(ex instanceof DomainException);
        assertTrue(ex instanceof RuntimeException);
        assertEquals("Invalid operation", ex.getMessage());
    }

    @Test
    @DisplayName("NotFoundException is a DomainException and preserves the message")
    void notFoundExceptionHierarchy() {
        NotFoundException ex = new NotFoundException("Resource not found");
        assertTrue(ex instanceof DomainException);
        assertTrue(ex instanceof RuntimeException);
        assertEquals("Resource not found", ex.getMessage());
    }

    @Test
    @DisplayName("ValidationException is a DomainException and preserves the message")
    void validationExceptionHierarchy() {
        ValidationException ex = new ValidationException("Invalid value");
        assertTrue(ex instanceof DomainException);
        assertTrue(ex instanceof RuntimeException);
        assertEquals("Invalid value", ex.getMessage());
    }
}
