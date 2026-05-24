package de.timkodiert.mokka.ui.helper;

import atlantafx.base.controls.PasswordTextField;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

public class PasswordTextFieldHelper {

    private PasswordTextFieldHelper() {
        // Statische Klasse
    }

    public static void installPasswordToggle(PasswordTextField passwordTextField) {
        FontIcon icon = FontIcon.of(BootstrapIcons.EYE);
        icon.setOnMouseClicked(event -> {
            icon.setIconCode(passwordTextField.getRevealPassword() ? BootstrapIcons.EYE : BootstrapIcons.EYE_SLASH);
            passwordTextField.setRevealPassword(!passwordTextField.getRevealPassword());
        });
        passwordTextField.setRight(icon);
    }
}
