package de.timkodiert.mokka.analysis;

import de.timkodiert.mokka.representation.HasRowType;
import de.timkodiert.mokka.representation.RowType;

public record TableRowData(String position, int value, RowType rowType) implements HasRowType {

    @Override
    public RowType getRowType() {
        return rowType;
    }
}
