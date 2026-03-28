package de.timkodiert.mokka.ui.control;

import java.io.IOException;
import java.net.URL;
import java.time.YearMonth;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import atlantafx.base.theme.Styles;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.exception.TechnicalException;
import de.timkodiert.mokka.i18n.LanguageManager;

public class MonthYearPicker extends VBox implements Initializable {

    private static final String RESOURCE_LOCATION = "/fxml/MonthYearPicker.fxml";

    @FXML
    @Getter
    private ChoiceBox<String> monthChoiceBox;
    @FXML
    private TextField yearTextField;
    @FXML
    private HBox widgetInnerContainer;

    private final Label label = new Label();
    private final Button resetBtn = new Button("", new FontIcon(BootstrapIcons.TRASH));
    private final ObjectProperty<YearMonth> value = new SimpleObjectProperty<>();

    @Getter
    @Setter // Getter und Setter für die Verwendung durch JavaFX / FXML
    private boolean nullable;

    private boolean muteListener = false;

    private LanguageManager languageManager;

    public void init(FXMLLoader fxmlLoader, LanguageManager languageManager) {
        this.languageManager = languageManager;

        fxmlLoader.setLocation(getClass().getResource(RESOURCE_LOCATION));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw TechnicalException.forFxmlNotFound(exception);
        }
    }

    private void updateValue() {
        if (muteListener) {
            return;
        }
        if (monthChoiceBox.getSelectionModel().isEmpty() || StringUtils.isEmpty(yearTextField.getText())) {
            value.set(null);
            return;
        }
        value.set(YearMonth.of(Integer.parseInt(yearTextField.getText()), monthChoiceBox.getSelectionModel().getSelectedIndex() + 1));
    }

    private void updateUi() {
        muteListener = true;
        if (this.value.get() != null) {
            monthChoiceBox.getSelectionModel().select(this.value.get().getMonthValue() - 1);
            yearTextField.setText("" + this.value.get().getYear());
        } else {
            monthChoiceBox.getSelectionModel().clearSelection();
            yearTextField.setText("");
        }
        muteListener = false;
    }

    public ObjectProperty<YearMonth> valueProperty() {
        return value;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthChoiceBox.getItems().addAll(languageManager.getMonths());

        resetBtn.setOnAction(event -> {
            monthChoiceBox.getSelectionModel().clearSelection();
            yearTextField.setText("");
        });

        UnaryOperator<Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9]\\d*)?")) {
                return change;
            }
            return null;
        };
        yearTextField.setTextFormatter(new TextFormatter<>(integerFilter));

        valueProperty().addListener((observable, oldValue, newValue) -> updateUi());
        monthChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateValue());
        yearTextField.textProperty().addListener((observable, oldValue, newValue) -> updateValue());

        // Styling setzen
        this.getChildren().addFirst(label);
        monthChoiceBox.getStyleClass().add(Styles.LEFT_PILL);
        yearTextField.getStyleClass().add(Styles.RIGHT_PILL);

        if (nullable) {
            widgetInnerContainer.getChildren().addAll(resetBtn);
        }
    }

    // Zur Verwendung durch JavaFX / FXML
    public String getLabel() {
        return label.getText();
    }

    // Zur Verwendung durch JavaFX / FXML
    public void setLabel(String labelText) {
        label.setText(labelText);
    }
}
