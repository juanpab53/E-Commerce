package com.ecommerce.shared.domain.valueobject;

import com.ecommerce.shared.domain.ValidationException;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (value == null) {
            throw new ValidationException("value must not be null");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new ValidationException("Invalid email address: " + value);
        }
    }
}
