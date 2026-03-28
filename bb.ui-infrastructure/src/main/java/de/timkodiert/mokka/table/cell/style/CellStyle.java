package de.timkodiert.mokka.table.cell.style;

import javafx.scene.control.TableCell;

import de.timkodiert.mokka.representation.RowType;

public interface CellStyle {

    void apply(TableCell<?, ?> cell, RowType rowType);
    void reset(TableCell<?, ?> cell);
}
