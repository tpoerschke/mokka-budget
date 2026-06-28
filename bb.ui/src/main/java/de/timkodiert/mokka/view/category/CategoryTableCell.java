package de.timkodiert.mokka.view.category;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.domain.CategoryDTO;

public class CategoryTableCell extends TableCell<CategoryDTO, CategoryDTO> {

    public CategoryTableCell() {
        super();
        setContentDisplay(ContentDisplay.RIGHT);
    }

    @Override
    protected void updateItem(CategoryDTO item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
            return;
        }
        Pane colorPane = new Pane();
        colorPane.setPrefSize(10, 10);
        setText(item.getName());
        setGraphic(colorPane);
        applyColor(colorPane, item.getColor());
    }

    private void applyColor(Pane colorPane, @Nullable String colorValue) {
        StringConverter<Color> colorStringConverter = Converters.get(Color.class);
        Color color = colorStringConverter.fromString(colorValue);
        if (color == null) {
            colorPane.setVisible(false);
            colorPane.setManaged(false);
            return;
        }
        colorPane.setVisible(true);
        colorPane.setManaged(true);
        colorPane.setStyle("-fx-background-color: " + colorValue + "; -fx-background-radius: 50%;");
    }
}
