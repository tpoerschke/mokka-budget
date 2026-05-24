package de.timkodiert.mokka.db.gatekeeper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.injector.ControllerFactory;
import de.timkodiert.mokka.properties.PropertiesService;

@Singleton
public class GatekeeperService {

    private static final Logger LOG = LoggerFactory.getLogger(GatekeeperService.class);

    private static final String ENCRYPTED_DB_SUFFIX = ".enc.db";

    private final LanguageManager languageManager;
    private final ControllerFactory controllerFactory;
    private final PropertiesService propertiesService;

    @Getter
    private boolean existsDatabase;
    @Getter
    private boolean isDatabaseEncrypted;
    private boolean passphraseValid;

    @Inject
    public GatekeeperService(LanguageManager languageManager, ControllerFactory controllerFactory, PropertiesService propertiesService) {
        this.languageManager = languageManager;
        this.controllerFactory = controllerFactory;
        this.propertiesService = propertiesService;
    }

    public void getDatabaseInformation() {
        String dbFilePath = propertiesService.getDbPath().replace("jdbc:sqlite:", "");
        existsDatabase = Files.exists(Paths.get(dbFilePath));
        isDatabaseEncrypted = dbFilePath.toLowerCase().endsWith(ENCRYPTED_DB_SUFFIX);
    }

    public void showAndGetDbPassphraseIfRequired() {
        if (existsDatabase && !isDatabaseEncrypted) {
            return;
        }
        if (existsDatabase) {
            loadAndShow("/fxml/PasswordPromptView.fxml");
        } else {
            loadAndShow("/fxml/EncryptionSetupView.fxml");
        }
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

    void storeEncryptedDbPath() {
        String dbPath = propertiesService.getDbPath();
        if (dbPath.toLowerCase().endsWith(ENCRYPTED_DB_SUFFIX)) {
            return;
        }
        dbPath = dbPath.replace(".db", dbPath);
        propertiesService.getProperties().replace(PropertiesService.DB, dbPath);
        try {
            propertiesService.store();
        } catch (IOException ioe) {
            StackTraceAlert.createAndLog("Error writing properties file!", ioe).showAndWait();
            Platform.exit();
        }
    }

    void setValidDatabasePassphrase(String passphrase) {
        propertiesService.setDbPassphrase(passphrase);
        passphraseValid = true;
    }

    boolean checkDatabaseConnection(String password) {
        try (Connection connection = DriverManager.getConnection(propertiesService.getFullDbPath(password))) {
            return connection.isValid(5);
        } catch (SQLException e) {
            LOG.warn("Could not connect to database with password.");
            LOG.warn(e.getMessage());
            return false;
        }
    }
}
