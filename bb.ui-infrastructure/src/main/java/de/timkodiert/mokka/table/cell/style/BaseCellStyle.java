package de.timkodiert.mokka.table.cell.style;

import javafx.scene.control.TableCell;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.representation.RowType;

public abstract class BaseCellStyle implements CellStyle {

    protected @Nullable CellStyle wrappedCellStyle;

    protected BaseCellStyle(@Nullable CellStyle wrappedCellStyle) {
        this.wrappedCellStyle = wrappedCellStyle;
    }

    protected void applyWrappedStyle(TableCell<?, ?> cell, RowType rowType) {
        if (wrappedCellStyle == null) {
            return;
        }
        wrappedCellStyle.apply(cell, rowType);
    }

    protected void resetWrappedStyle(TableCell<?, ?> cell) {
        if (wrappedCellStyle == null) {
            return;
        }
        wrappedCellStyle.reset(cell);
    }
}
