package de.timkodiert.mokka.db.gatekeeper;

import java.net.URL;
import java.util.ResourceBundle;

import atlantafx.base.controls.PasswordTextField;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.ui.helper.PasswordTextFieldHelper;
import de.timkodiert.mokka.view.View;

public class PasswordPromptView implements View, Initializable {

    @FXML
    private PasswordTextField passwordField;

    private final LanguageManager languageManager;
    private final GatekeeperService service;

    @Inject
    public  PasswordPromptView(LanguageManager languageManager, GatekeeperService service) {
        this.languageManager = languageManager;
        this.service = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PasswordTextFieldHelper.installPasswordToggle(passwordField);
    }

    @FXML
    private void processPassword() {
        if (!service.checkDatabaseConnection(passwordField.getPassword())) {
            DialogFactory.buildErrorDialog(languageManager.get("passwordPromptView.alert.incorrectPassword")).showAndWait();
            return;
        }
        service.setValidDatabasePassphrase(passwordField.getPassword());
        closeDialog();
    }

    private void closeDialog() {
        passwordField.getScene().getWindow().hide();
    }
}
