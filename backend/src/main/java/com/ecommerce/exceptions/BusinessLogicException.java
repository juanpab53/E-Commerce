package com.ecommerce.exceptions;

public class BusinessLogicException extends RuntimeException{
    public BusinessLogicException(String message) {
        super(message);
    }
}
