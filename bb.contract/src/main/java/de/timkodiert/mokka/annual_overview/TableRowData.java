package de.timkodiert.mokka.annual_overview;

import java.util.Map;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.representation.HasRowType;
import de.timkodiert.mokka.representation.RowType;

public record TableRowData(int id, String label, Map<Integer, Integer> monthValueMap, Reference<CategoryDTO> category, RowType type) implements HasRowType {

    public static TableRowData forCategory(Reference<CategoryDTO> category) {
        return new TableRowData(category.id(), category.name(), Map.of(), category, RowType.CATEGORY_GROUP);
    }

    @Override
    public RowType getRowType() {
        return type;
    }
}
