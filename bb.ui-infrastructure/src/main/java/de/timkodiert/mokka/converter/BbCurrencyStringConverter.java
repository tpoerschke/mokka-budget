package de.timkodiert.mokka.converter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;
import javax.inject.Inject;

import javafx.util.StringConverter;

import de.timkodiert.mokka.exception.TechnicalException;

public class BbCurrencyStringConverter extends StringConverter<Integer> {

    private final NumberFormat format;

    @Inject
    public BbCurrencyStringConverter() {
        format = NumberFormat.getCurrencyInstance(Locale.GERMAN);
        format.setCurrency(Currency.getInstance("EUR"));
    }

    @Override
    public String toString(Integer obj) {
        if (obj == null) {
            return "";
        }
        return format.format(obj / 100.0);
    }

    @Override
    public Integer fromString(String str) {
        try {
            return str.isEmpty() ? null : (int) (format.parse(str).doubleValue() * 100);
        } catch (ParseException e) {
            throw TechnicalException.forProgrammingError(e);
        }
    }
}
