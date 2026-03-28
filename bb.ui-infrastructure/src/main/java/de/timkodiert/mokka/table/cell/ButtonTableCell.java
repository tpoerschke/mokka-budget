package de.timkodiert.mokka.table.cell;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ButtonTableCell<S> extends TableCell<S, S> {

    private String buttonText;
    private Consumer<S> onClick;

    @Override
    public void updateItem(S item, boolean empty) {
        super.updateItem(item, empty);
        setEditable(false);
        if(!empty) {             
            Button button = new Button(buttonText);   
            button.setOnAction(event -> onClick.accept(item));  
            setGraphic(button);
        } else {
            setGraphic(null);
        }
    }   
}
