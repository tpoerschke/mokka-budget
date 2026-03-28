package de.timkodiert.mokka.table.cell.style;

import javafx.scene.control.TableCell;

import de.timkodiert.mokka.representation.RowType;

public class GrayBackground extends BaseCellStyle {

    private static final String STYLE_CLASS = "col-background-gray";

    public GrayBackground() {
        super(null);
    }

    public GrayBackground(CellStyle wrappedCellStyle) {
        super(wrappedCellStyle);
    }

    @Override
    public void apply(TableCell<?, ?> cell, RowType rowType) {
        applyWrappedStyle(cell, rowType);
        if (rowType == RowType.CATEGORY_GROUP) {
            cell.getStyleClass().add(STYLE_CLASS);
        }
    }

    @Override
    public void reset(TableCell<?, ?> cell) {
        resetWrappedStyle(cell);
        cell.getStyleClass().remove(STYLE_CLASS);
    }
}
