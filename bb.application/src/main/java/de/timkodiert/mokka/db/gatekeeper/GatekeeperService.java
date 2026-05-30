package de.timkodiert.mokka.db.gatekeeper;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.timkodiert.mokka.db.gatekeeper.DatabaseFileStateDetector.DatabaseFileState;
import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.injector.ControllerFactory;
import de.timkodiert.mokka.properties.PropertiesService;

@Singleton
public class GatekeeperService {

    private static final Logger LOG = LoggerFactory.getLogger(GatekeeperService.class);

    private final LanguageManager languageManager;
    private final ControllerFactory controllerFactory;
    private final PropertiesService propertiesService;
    private final DatabaseEncryptionService encryptionService;

    @Getter
    private boolean existsDatabase;
    @Getter
    private boolean isDatabaseEncrypted;
    private boolean passphraseValid;

    @Inject
    public GatekeeperService(LanguageManager languageManager,
                             ControllerFactory controllerFactory,
                             PropertiesService propertiesService,
                             DatabaseEncryptionService encryptionService) {
        this.languageManager = languageManager;
        this.controllerFactory = controllerFactory;
        this.propertiesService = propertiesService;
        this.encryptionService = encryptionService;
    }

    public void getDatabaseInformation() {
        DatabaseFileState state = DatabaseFileStateDetector.detect(propertiesService.getDbPath());
        existsDatabase = state != DatabaseFileState.NOT_FOUND;
        isDatabaseEncrypted = state == DatabaseFileState.ENCRYPTED_OR_INVALID;
    }

    public void showAndGetDbPassphraseIfRequired() {
        if (existsDatabase && !isDatabaseEncrypted) {
            return;
        }
        loadAndShow(existsDatabase ? "/fxml/PasswordPromptView.fxml" : "/fxml/EncryptionSetupView.fxml");
    }

    private void loadAndShow(String viewResource) {
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource(viewResource));
            viewLoader.setControllerFactory(controllerFactory::create);
            viewLoader.setResources(languageManager.getResourceBundle());

            Stage stage = new Stage();
            stage.setTitle("MOKKA Budget – " + getVersion());
            stage.setScene(new Scene(viewLoader.load()));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/general-styles.css").toExternalForm());
            stage.setWidth(500);
            stage.setOnHidden(event -> {
                if (passphraseValid) {
                    return; // Fortsetzen und Applikation starten
                }
                Platform.exit();
                System.exit(1);
            });
            stage.showAndWait();
        } catch (Exception e) {
            StackTraceAlert.createAndLog(languageManager.get("alert.viewCouldNotBeOpened"), e).showAndWait();
        }
    }

    private String getVersion() {
        return "Version " + getClass().getPackage().getImplementationVersion();
    }

    void setValidDatabasePassphrase(String passphrase) {
        propertiesService.setDbPassphrase(passphrase);
        passphraseValid = true;
    }

    void proceedWithoutEncryption() {
        passphraseValid = true;
    }

    boolean checkDatabaseConnection(String password) {
        if (!encryptionService.isPasswordValid(password)) {
            LOG.warn("Could not connect to encrypted database.");
            return false;
        }
        return true;
    }
}
