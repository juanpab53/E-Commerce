package com.ecommerce.shared.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ecommerce.shared.domain.ValidationException;

class MoneyTest {

    private static final Currency COP = Currency.getInstance("COP");

    @Test
    @DisplayName("of(double, String) builds the money with the given amount and currency")
    void buildsFromDoubleAndCode() {
        Money money = Money.of(19.99, "COP");
        assertEquals(new BigDecimal("19.99"), money.amount());
        assertEquals(COP, money.currency());
    }

    @Test
    @DisplayName("of(double, Currency) builds the money with the given currency")
    void buildsFromDoubleAndCurrency() {
        Money money = Money.of(10.5, COP);
        assertEquals(new BigDecimal("10.5"), money.amount());
        assertEquals(COP, money.currency());
    }

    @Test
    @DisplayName("of(BigDecimal, Currency) builds the money with an exact amount")
    void buildsFromBigDecimalAndCurrency() {
        Money money = Money.of(new BigDecimal("1234.56"), COP);
        assertEquals(new BigDecimal("1234.56"), money.amount());
        assertEquals(COP, money.currency());
    }

    @Test
    @DisplayName("rejects negative amounts")
    void rejectsNegativeAmount() {
        assertThrows(ValidationException.class, () -> Money.of(-1, COP));
    }

    @Test
    @DisplayName("rejects null amount")
    void rejectsNullAmount() {
        assertThrows(ValidationException.class, () -> Money.of((BigDecimal) null, COP));
    }

    @Test
    @DisplayName("rejects null currency")
    void rejectsNullCurrency() {
        assertThrows(ValidationException.class, () -> Money.of(10, (Currency) null));
    }

    @Test
    @DisplayName("rejects invalid currency code")
    void rejectsInvalidCurrencyCode() {
        assertThrows(ValidationException.class, () -> Money.of(10, "NOT-A-CURRENCY"));
    }
}
