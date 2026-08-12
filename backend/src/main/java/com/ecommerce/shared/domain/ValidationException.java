package com.ecommerce.shared.domain;

public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message);
    }
}
