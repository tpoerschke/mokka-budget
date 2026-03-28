package de.timkodiert.mokka.converter;

import javafx.util.StringConverter;

import de.timkodiert.mokka.domain.Reference;

public class ReferenceStringConverter<T> extends StringConverter<Reference<T>> {

    @Override
    public String toString(Reference<T> object) {
        return object != null ? object.name() : ConverterConstants.NULL_STRING;
    }

    @Override
    public Reference<T> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}
