package de.timkodiert.mokka.view.importer;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Consumer;
import javax.inject.Inject;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.domain.FixedTurnoverCrudService;
import de.timkodiert.mokka.domain.FixedTurnoverDTO;
import de.timkodiert.mokka.domain.ImportRuleDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.TurnoverDirection;
import de.timkodiert.mokka.importer.ImportInformation;
import de.timkodiert.mokka.ui.control.MoneyTextField;
import de.timkodiert.mokka.view.View;

public class FixedTurnoverWizardView implements View, Initializable {

    private final FixedTurnoverCrudService fixedTurnoverCrudService;
    private final DialogFactory dialogFactory;

    private final ObjectProperty<ImportInformation> importInformation = new SimpleObjectProperty<>();

    @FXML
    private TextField positionTextField;
    @FXML
    private MoneyTextField valueTextField;
    @FXML
    private ComboBox<TurnoverDirection> directionComboBox;
    @FXML
    private CheckBox importActiveCheckbox;
    @FXML
    private TextField importReceiverTextField;
    @FXML
    private TextField importReferenceTextField;

    @Setter
    private Consumer<Reference<FixedTurnoverDTO>> onSaveCallback;

    @Inject
    public FixedTurnoverWizardView(FixedTurnoverCrudService fixedTurnoverCrudService, DialogFactory dialogFactory) {
        this.fixedTurnoverCrudService = fixedTurnoverCrudService;
        this.dialogFactory = dialogFactory;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        valueTextField.getTextField().setEditable(false);
        directionComboBox.setConverter(Converters.get(TurnoverDirection.class));
        directionComboBox.getItems().setAll(TurnoverDirection.values());

        importInformation.addListener((observable, oldVal, newVal) -> {
            positionTextField.setText(newVal.receiverProperty().get());
            directionComboBox.getSelectionModel().select(TurnoverDirection.valueOf(newVal.amountProperty().getValue()));
            valueTextField.setValue(Math.abs(newVal.amountProperty().getValue()));
            importActiveCheckbox.setSelected(true);
            importReceiverTextField.setText(newVal.receiverProperty().get());
            importReferenceTextField.setText(newVal.referenceProperty().get());
        });
    }

    @FXML
    private void createTurnover(ActionEvent event) {
        ImportRuleDTO importRule = ImportRuleDTO.create(importActiveCheckbox.isSelected(), importReceiverTextField.getText(), importReferenceTextField.getText());
        FixedTurnoverDTO turnover = FixedTurnoverDTO.create(positionTextField.getText(), directionComboBox.getSelectionModel().getSelectedItem(), importRule);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<FixedTurnoverDTO>> violationSet = validator.validate(turnover);

        if (!violationSet.isEmpty()) {
            dialogFactory.buildValidationErrorDialog(violationSet).showAndWait();
            return;
        }

        int id = fixedTurnoverCrudService.create(turnover);
        onSaveCallback.accept(new Reference<>(FixedTurnoverDTO.class, id, turnover.getPosition()));

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public ObjectProperty<ImportInformation> importInformationProperty() {
        return importInformation;
    }
}
