package de.timkodiert.mokka.converter;

import javafx.util.StringConverter;

import de.timkodiert.mokka.domain.CategoryGroupDTO;

import static de.timkodiert.mokka.util.ObjectUtils.nvl;

public class CategoryGroupStringConverter extends StringConverter<CategoryGroupDTO> {

    @Override
    public String toString(CategoryGroupDTO categoryGroup) {
        return nvl(categoryGroup, CategoryGroupDTO::getName, ConverterConstants.NULL_STRING);
    }

    @Override
    public CategoryGroupDTO fromString(String s) {
        throw new UnsupportedOperationException();
    }
}
