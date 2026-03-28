package de.timkodiert.mokka.table.cell;

import java.util.Objects;
import java.util.function.BiConsumer;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class TextFieldTableCell<S> extends TableCell<S, String> {

    private final TextField textField = new TextField();
    private final BiConsumer<String, S> editHandler;

    public TextFieldTableCell(BiConsumer<String, S> editHandler) {
        this.editHandler = editHandler;

        textField.setOnAction(e -> handleEdit());
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (Boolean.FALSE.equals(isNowFocused)) {
                handleEdit();
            }
        });
    }

    private void handleEdit() {
        String newVal = textField.getText();
        String oldVal = getItem();
        if (Objects.equals(oldVal, newVal)) {
            return;
        }
        editHandler.accept(newVal, getTableRow().getItem());
        updateItem(newVal, false);
        cancelEdit();
    }

    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(getItem());
        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            textField.setText(item);
            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(item);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }
}
