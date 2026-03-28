package de.timkodiert.mokka.table.row;

import javafx.scene.control.TableRow;

import de.timkodiert.mokka.representation.HasRowType;
import de.timkodiert.mokka.representation.RowType;


public class BoldTableRow<I extends HasRowType> extends TableRow<I> {

    private static final String CSS_CLASS = "text-bold";

    // Die Rows dieses Typs werden fett dargestellt
    private final RowType type;

    public BoldTableRow(RowType type) {
        super();
        this.type = type;
    }

    @Override
    protected void updateItem(I item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item.getRowType() == type) {
            getStyleClass().add(CSS_CLASS);
        } else {
            getStyleClass().remove(CSS_CLASS);
        }
    }
}
