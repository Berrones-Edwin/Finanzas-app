package com.bitly.converters;

import java.util.Currency;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        return currency != null ? currency.getCurrencyCode() : null;
    }

    @Override
    public Currency convertToEntityAttribute(String code) {

        return code != null ? Currency.getInstance(code) : null;
    }

}
