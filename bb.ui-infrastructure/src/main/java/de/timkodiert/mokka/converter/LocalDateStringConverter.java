package de.timkodiert.mokka.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

public class LocalDateStringConverter extends StringConverter<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public String toString(LocalDate dateValue) {
        return formatter.format(dateValue);
    }

    @Override
    public LocalDate fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
