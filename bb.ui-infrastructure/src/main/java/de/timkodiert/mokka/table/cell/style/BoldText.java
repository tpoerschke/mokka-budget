package de.timkodiert.mokka.table.cell.style;

import javafx.scene.control.TableCell;

import de.timkodiert.mokka.representation.RowType;

public class BoldText extends BaseCellStyle {

    private static final String STYLE_CLASS = "col-bold-text";

    public BoldText() {
        super(null);
    }

    public BoldText(CellStyle wrappedCellStyle) {
        super(wrappedCellStyle);
    }

    @Override
    public void apply(TableCell<?, ?> cell, RowType rowType) {
        applyWrappedStyle(cell, rowType);

        int cellRowIndex = cell.getTableRow().getIndex();
        int maxRowIndex = cell.getTableView().getItems().size() - 1;
        boolean isLastRow = cellRowIndex == maxRowIndex;

        if (rowType == RowType.CATEGORY_GROUP || isLastRow) {
            cell.getStyleClass().add(STYLE_CLASS);
        }
    }

    @Override
    public void reset(TableCell<?, ?> cell) {
        resetWrappedStyle(cell);
        cell.getStyleClass().remove(STYLE_CLASS);
    }
}
