package de.timkodiert.mokka.dialog;

import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import jakarta.validation.ConstraintViolation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

public class DialogFactory {

    public static final ButtonType CANCEL = new ButtonType("Abbrechen", ButtonData.CANCEL_CLOSE);
    public static final ButtonType SAVE_CHANGES = new ButtonType("Änderungen speichern", ButtonData.YES);
    public static final ButtonType DISCARD_CHANGES = new ButtonType("Änderungen verwerfen", ButtonData.NO);

    @Inject
    public DialogFactory() {
        // Default-Konstruktor für Inject-Annotation
    }

    public Alert buildConfirmationDialog() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Wie soll mit den Änderungen verfahren werden?",
                SAVE_CHANGES, DISCARD_CHANGES, CANCEL);
        alert.setHeaderText("Es liegen ungespeicherte Änderungen vor.");
        return alert;
    }

    public static Alert buildInformationDialog(String information) {
        return new Alert(AlertType.INFORMATION, information);
    }

    public <T> Alert buildValidationErrorDialog(Set<ConstraintViolation<T>> violationSet) {
        String informationStr = violationSet.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("\n"));
        return buildInformationDialog(informationStr);
    }

    public Alert buildErrorDialog(String message) {
        return new Alert(AlertType.ERROR, message);
    }
}
