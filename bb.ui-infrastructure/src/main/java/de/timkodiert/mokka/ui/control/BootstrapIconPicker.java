package de.timkodiert.mokka.ui.control;

import java.util.Arrays;
import java.util.Comparator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import org.jspecify.annotations.Nullable;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.ui.util.BootstrapIconUtil;

public class BootstrapIconPicker extends ComboBox<BootstrapIcons> {

    private final ObjectProperty<@Nullable String> iconNameProperty = new SimpleObjectProperty<>();

    public BootstrapIconPicker() {
        setEditable(false);
        getItems().add(null);
        getItems().addAll(Arrays.stream(BootstrapIcons.values())
                                .sorted(Comparator.comparing(Enum::name))
                                .toList());
        setCellFactory(listView -> new IconListCell());
        setButtonCell(new IconListCell());
        valueProperty().addListener((obs, oldValue, newValue) -> iconNameProperty.set(BootstrapIconUtil.toIconName(newValue)));
        iconNameProperty.addListener((obs, oldValue, newValue) -> {
            if (!BootstrapIconUtil.toIconName(getValue()).equals(newValue)) {
                setValue(BootstrapIconUtil.parse(newValue));
            }
        });
    }

    public ObjectProperty<@Nullable String> iconNameProperty() {
        return iconNameProperty;
    }

    public void setIconName(@Nullable String iconName) {
        iconNameProperty.set(iconName);
    }

    public @Nullable String getIconName() {
        return iconNameProperty.get();
    }

    private static class IconListCell extends ListCell<BootstrapIcons> {

        private final HBox content = new HBox(8);
        private final FontIcon iconView = new FontIcon();
        private final Label nameLabel = new Label();
        private final Region spacer = new Region();

        IconListCell() {
            HBox.setHgrow(spacer, Priority.ALWAYS);
            content.getChildren().addAll(iconView, nameLabel, spacer);
        }

        @Override
        protected void updateItem(BootstrapIcons item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
                return;
            }
            if (item == null) {
                setText("—");
                setGraphic(null);
                return;
            }
            iconView.setIconCode(item);
            nameLabel.setText(item.name());
            setText(null);
            setGraphic(content);
        }
    }
}
