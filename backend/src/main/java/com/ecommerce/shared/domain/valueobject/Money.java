package com.ecommerce.shared.domain.valueobject;

import java.math.BigDecimal;
import java.util.Currency;

import com.ecommerce.shared.domain.ValidationException;

/**
 * Value object for monetary amounts.
 *
 * <p>Carries both the amount and its currency. The database schema stores only the
 * amount ({@code NUMERIC}), never the currency: this VO is built at the application
 * boundary using the application base currency ({@code app.base.currency}), keeping the
 * domain FX-ready for a future currency-conversion sprint.
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (amount == null) {
            throw new ValidationException("amount must not be null");
        }
        if (currency == null) {
            throw new ValidationException("currency must not be null");
        }
        if (amount.signum() < 0) {
            throw new ValidationException("amount must not be negative");
        }
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(double value, Currency currency) {
        return new Money(BigDecimal.valueOf(value), currency);
    }

    public static Money of(double value, String currencyCode) {
        try {
            return new Money(BigDecimal.valueOf(value), Currency.getInstance(currencyCode));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ValidationException("Invalid currency code: " + currencyCode);
        }
    }
}
