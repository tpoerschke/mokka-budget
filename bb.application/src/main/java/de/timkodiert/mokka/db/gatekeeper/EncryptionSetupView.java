package de.timkodiert.mokka.db.gatekeeper;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.ui.helper.PasswordTextFieldHelper;
import de.timkodiert.mokka.view.View;

public class EncryptionSetupView implements View, Initializable {

    @FXML
    private PasswordTextField passwordField;
    @FXML
    private PasswordTextField passwordRetypeField;
    @FXML
    private Button noDataEncButton;
    @FXML
    private Label passwortLossLabel;

    private final LanguageManager languageManager;
    private final GatekeeperService service;

    @Inject
    public EncryptionSetupView(LanguageManager languageManager, GatekeeperService service) {
        this.languageManager = languageManager;
        this.service = service;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        noDataEncButton.setTooltip(new Tooltip(languageManager.get("encryptionSetupView.tooltip.encryptionAfterward")));
        passwortLossLabel.setGraphic(new FontIcon(BootstrapIcons.EXCLAMATION_CIRCLE));
        passwortLossLabel.getStyleClass().add(Styles.WARNING);
        passwortLossLabel.setGraphicTextGap(10);
        PasswordTextFieldHelper.installPasswordToggle(passwordField);
        PasswordTextFieldHelper.installPasswordToggle(passwordRetypeField);
    }

    @FXML
    private void processPassword() {
        if (!Objects.equals(passwordField.getPassword(), passwordRetypeField.getPassword())) {
            DialogFactory.buildErrorDialog(languageManager.get("encryptionSetupView.alert.passwordsNotEqual")).showAndWait();
            return;
        }
        service.setValidDatabasePassphrase(passwordField.getPassword());
        closeDialog();
    }

    @FXML
    private void proceedWithoutEncryption() {
        service.proceedWithoutEncryption();
        closeDialog();
    }

    private void closeDialog() {
        passwordField.getScene().getWindow().hide();
    }
}
