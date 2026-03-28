package de.timkodiert.mokka.table.cell;

import java.util.function.BiConsumer;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;

public class EditableCheckBoxCell<S> extends TableCell<S, Boolean> {

    private final CheckBox checkBox = new CheckBox();

    public EditableCheckBoxCell(BiConsumer<Boolean, S> checkHandler) {
        checkBox.setOnAction(e -> checkHandler.accept(checkBox.isSelected(), getTableRow().getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            checkBox.setSelected(item);
            setGraphic(checkBox);
        }
    }
}
