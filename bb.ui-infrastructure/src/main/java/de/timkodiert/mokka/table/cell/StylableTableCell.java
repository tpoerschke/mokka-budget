package de.timkodiert.mokka.table.cell;

import java.util.Optional;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;

import de.timkodiert.mokka.representation.HasRowType;
import de.timkodiert.mokka.representation.RowType;
import de.timkodiert.mokka.table.cell.style.CellStyle;

public class StylableTableCell<S extends HasRowType, T> extends TableCell<S, T> {

    private final CellStyle cellStyle;

    public StylableTableCell(CellStyle cellStyle) {
        super();
        this.cellStyle = cellStyle;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        RowType rowType = Optional.ofNullable(getTableRow()).map(TableRow::getItem).map(HasRowType::getRowType).orElse(RowType.EMPTY);
        cellStyle.reset(this);
        cellStyle.apply(this, rowType);
        setText(Optional.ofNullable(item).map(Object::toString).orElse(""));
    }
}
