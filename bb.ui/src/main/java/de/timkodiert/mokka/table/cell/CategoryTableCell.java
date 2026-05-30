package de.timkodiert.mokka.table.cell;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.ui.util.BootstrapIconUtil;
import de.timkodiert.mokka.ui.util.ColorUtil;

public class CategoryTableCell extends TableCell<CategoryDTO, CategoryDTO> {

    private final HBox content = new HBox(8);
    private final FontIcon iconView = new FontIcon();
    private final Label nameLabel = new Label();
    private final Region spacer = new Region();

    public CategoryTableCell() {
        HBox.setHgrow(spacer, Priority.ALWAYS);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(4, 8, 4, 8));
        content.getChildren().addAll(iconView, nameLabel, spacer);
        iconView.setVisible(false);
        iconView.setManaged(false);
    }

    @Override
    protected void updateItem(CategoryDTO item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
            applyBackground(null);
            return;
        }

        nameLabel.setText(item.getName());
        BootstrapIcons bootstrapIcon = BootstrapIconUtil.parse(item.getIcon());
        if (bootstrapIcon != null) {
            iconView.setIconCode(bootstrapIcon);
            iconView.setVisible(true);
            iconView.setManaged(true);
        } else {
            iconView.setVisible(false);
            iconView.setManaged(false);
        }

        applyBackground(item.getColor());
        setText(null);
        setGraphic(content);
    }

    private void applyBackground(@Nullable String colorValue) {
        Color background = ColorUtil.parse(colorValue);
        if (background == null) {
            content.setStyle("");
            return;
        }
        String textColor = background.grayscale().getBrightness() > 0.6 ? "black" : "white";
        content.setStyle("-fx-background-color: " + ColorUtil.toHex(background) + "; -fx-background-radius: 4; -fx-text-fill: " + textColor + ";");
        nameLabel.setStyle("-fx-text-fill: " + textColor + ";");
    }
}
