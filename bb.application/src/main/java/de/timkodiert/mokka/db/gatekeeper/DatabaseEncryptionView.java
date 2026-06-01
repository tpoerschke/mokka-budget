package de.timkodiert.mokka.db.gatekeeper;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.ui.helper.PasswordTextFieldHelper;
import de.timkodiert.mokka.view.View;

public class DatabaseEncryptionView implements View, Initializable {

    public static final String OPERATION_FAILED = "databaseEncryptionView.alert.operationFailed";
    public static final String PASSWORDS_NOT_EQUAL = "encryptionSetupView.alert.passwordsNotEqual";

    @FXML
    private Label statusLabel;
    @FXML
    private Label passwordLossLabel;
    @FXML
    private VBox enableEncryptionBox;
    @FXML
    private PasswordTextField enablePasswordField;
    @FXML
    private PasswordTextField enablePasswordRetypeField;
    @FXML
    private VBox manageEncryptionBox;
    @FXML
    private PasswordTextField currentPasswordField;
    @FXML
    private PasswordTextField newPasswordField;
    @FXML
    private PasswordTextField newPasswordRetypeField;
    @FXML
    private Button enableEncryptionButton;
    @FXML
    private Button disableEncryptionButton;
    @FXML
    private Button changePasswordButton;

    private final LanguageManager languageManager;
    private final GatekeeperService gatekeeperService;
    private final DatabaseEncryptionService encryptionService;

    @Inject
    public DatabaseEncryptionView(LanguageManager languageManager,
                                  GatekeeperService gatekeeperService,
                                  DatabaseEncryptionService encryptionService) {
        this.languageManager = languageManager;
        this.gatekeeperService = gatekeeperService;
        this.encryptionService = encryptionService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordLossLabel.setGraphic(new FontIcon(BootstrapIcons.EXCLAMATION_CIRCLE));
        passwordLossLabel.getStyleClass().add(Styles.WARNING);
        passwordLossLabel.setGraphicTextGap(10);
        statusLabel.setGraphicTextGap(10);

        PasswordTextFieldHelper.installPasswordToggle(enablePasswordField);
        PasswordTextFieldHelper.installPasswordToggle(enablePasswordRetypeField);
        PasswordTextFieldHelper.installPasswordToggle(currentPasswordField);
        PasswordTextFieldHelper.installPasswordToggle(newPasswordField);
        PasswordTextFieldHelper.installPasswordToggle(newPasswordRetypeField);

        enableEncryptionButton.disableProperty().bind(enablePasswordField.textProperty().isEmpty());
        disableEncryptionButton.disableProperty().bind(currentPasswordField.textProperty().isEmpty());
        changePasswordButton.disableProperty().bind(currentPasswordField.textProperty().isEmpty());

        refreshViewState();
    }

    private void refreshViewState() {
        gatekeeperService.getDatabaseInformation();
        boolean encrypted = gatekeeperService.isDatabaseEncrypted();
        if (encrypted) {
            statusLabel.setGraphic(FontIcon.of(BootstrapIcons.LOCK));
            statusLabel.setText(languageManager.get("databaseEncryptionView.label.statusEnabled"));
            statusLabel.getStyleClass().remove(Styles.WARNING);
            statusLabel.getStyleClass().add(Styles.SUCCESS);
        } else {
            statusLabel.setGraphic(FontIcon.of(BootstrapIcons.UNLOCK));
            statusLabel.setText(languageManager.get("databaseEncryptionView.label.statusDisabled"));
            statusLabel.getStyleClass().remove(Styles.SUCCESS);
            statusLabel.getStyleClass().add(Styles.WARNING);
        }
        enableEncryptionBox.setVisible(!encrypted);
        enableEncryptionBox.setManaged(!encrypted);
        manageEncryptionBox.setVisible(encrypted);
        manageEncryptionBox.setManaged(encrypted);

        newPasswordField.clear();
        newPasswordRetypeField.clear();
        currentPasswordField.clear();
        currentPasswordField.clear();
        enablePasswordField.clear();
        enablePasswordRetypeField.clear();
    }

    @FXML
    private void enableEncryption() {
        if (!passwordsMatch(enablePasswordField, enablePasswordRetypeField)) {
            DialogFactory.buildErrorDialog(languageManager.get(PASSWORDS_NOT_EQUAL)).showAndWait();
            return;
        }
        try {
            encryptionService.enableEncryption(enablePasswordField.getPassword());
            refreshViewState();
        } catch (Exception e) {
            String backupFile = encryptionService.getBackupPath();
            String message = String.format(languageManager.get(OPERATION_FAILED), backupFile);
            StackTraceAlert.createAndLog(message, e).showAndWait();
        }
    }

    @FXML
    private void changePassword() {
        if (!encryptionService.isPasswordValid(currentPasswordField.getPassword())) {
            DialogFactory.buildErrorDialog(languageManager.get("passwordPromptView.alert.incorrectPassword")).showAndWait();
            return;
        }
        if (!passwordsMatch(newPasswordField, newPasswordRetypeField)) {
            DialogFactory.buildErrorDialog(languageManager.get(PASSWORDS_NOT_EQUAL)).showAndWait();
            return;
        }
        try {
            encryptionService.changePassword(currentPasswordField.getPassword(), newPasswordField.getPassword());
            refreshViewState();
        } catch (Exception e) {
            StackTraceAlert.createAndLog(languageManager.get(OPERATION_FAILED), e).showAndWait();
        }
    }

    @FXML
    private void disableEncryption() {
        if (!encryptionService.isPasswordValid(currentPasswordField.getPassword())) {
            DialogFactory.buildErrorDialog(languageManager.get("passwordPromptView.alert.incorrectPassword")).showAndWait();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                                       languageManager.get("databaseEncryptionView.alert.confirmDisable"),
                                       ButtonType.OK,
                                       ButtonType.CANCEL);
        confirmation.setHeaderText(null);
        confirmation.showAndWait().filter(ButtonType.OK::equals).ifPresent(button -> disableEncryptionConfirmed());
    }

    private void disableEncryptionConfirmed() {
        try {
            encryptionService.disableEncryption(currentPasswordField.getPassword());
            refreshViewState();
        } catch (Exception e) {
            StackTraceAlert.createAndLog(languageManager.get(OPERATION_FAILED), e).showAndWait();
        }
    }

    @FXML
    private void closeDialog() {
        statusLabel.getScene().getWindow().hide();
    }

    private boolean passwordsMatch(PasswordTextField first, PasswordTextField second) {
        return Objects.equals(first.getPassword(), second.getPassword());
    }
}
