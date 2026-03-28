package de.timkodiert.mokka.ui.control;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

public class AutoCompleteTextField extends TextField {

    private final ContextMenu contextMenu = new ContextMenu();

    @Getter
    @Setter
    private int maxEntries = 10;

    @Getter
    private final Set<String> availableEntries = new HashSet<>();

    public AutoCompleteTextField() {
        super();

        textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && focusedProperty().get()) {
                List<String> suitableList = findSuitableEntries(newValue);
                if (!suitableList.isEmpty()) {
                    generateContextMenuItems(suitableList);
                    showContextMenu();
                } else {
                    contextMenu.hide();
                }
            }
        });
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue) && textProperty().get() != null) {
                List<String> suitableList = findSuitableEntries(textProperty().get());
                if (!suitableList.isEmpty()) {
                    generateContextMenuItems(suitableList);
                    showContextMenu();
                } else {
                    contextMenu.hide();
                }
            }
        });
    }

    private void showContextMenu() {
        contextMenu.show(this, Side.BOTTOM, 0, 0);
    }

    private void generateContextMenuItems(List<String> suitableEntries) {
        contextMenu.getItems().clear();
        suitableEntries.subList(0, Math.min(maxEntries, suitableEntries.size())).forEach(str -> {
            MenuItem item = new MenuItem(str);
            item.setOnAction(event -> setText(str));
            contextMenu.getItems().add(item);
        });
    }

    private List<String> findSuitableEntries(String input) {
        return availableEntries.stream().filter(str -> str.toLowerCase().contains(input.toLowerCase())).toList();
    }
}
