package de.timkodiert.mokka.validation;

import java.util.concurrent.Callable;

import javafx.scene.control.Control;

public record CustomValidation(Control control, Callable<ValidationResult> validationSupplier) {
}
