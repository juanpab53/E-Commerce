package com.ecommerce.shared.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ecommerce.shared.domain.ValidationException;

class EmailTest {

    @Test
    @DisplayName("accepts valid email addresses")
    void acceptsValidEmails() {
        assertEquals("user@example.com", new Email("user@example.com").value());
        assertEquals("user.name+tag@sub.domain.co", new Email("user.name+tag@sub.domain.co").value());
    }

    @Test
    @DisplayName("rejects invalid email addresses")
    void rejectsInvalidEmails() {
        assertThrows(ValidationException.class, () -> new Email("plainaddress"));
        assertThrows(ValidationException.class, () -> new Email("user@"));
        assertThrows(ValidationException.class, () -> new Email("@domain.com"));
        assertThrows(ValidationException.class, () -> new Email("user with space@domain.com"));
        assertThrows(ValidationException.class, () -> new Email("user@domain.c"));
    }

    @Test
    @DisplayName("rejects null email")
    void rejectsNullEmail() {
        assertThrows(ValidationException.class, () -> new Email(null));
    }
}
