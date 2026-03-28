package de.timkodiert.mokka.table.cell;

import javafx.scene.control.TableCell;

import de.timkodiert.mokka.converter.ReferenceStringConverter;
import de.timkodiert.mokka.domain.Reference;

public class ReferenceTableCell<S, T> extends TableCell<S, Reference<T>> {

    private final ReferenceStringConverter<T> converter;

    public ReferenceTableCell() {
        converter = new ReferenceStringConverter<>();
    }

    @Override
    protected void updateItem(Reference<T> item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty ? "" : converter.toString(item));
    }
}
