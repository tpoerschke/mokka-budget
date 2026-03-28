package de.timkodiert.mokka.table.cell;

import java.time.YearMonth;

import javafx.scene.control.TableCell;

public class YearMonthTableCell<S> extends TableCell<S, YearMonth> {

    @Override
    protected void updateItem(YearMonth item, boolean empty) {
        super.updateItem(item, empty);
        setText(item == null ? "" : String.format("%2d.%d", item.getMonth().getValue(), item.getYear()));
    }
}
