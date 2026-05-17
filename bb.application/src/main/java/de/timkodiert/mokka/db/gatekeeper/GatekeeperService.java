package de.timkodiert.mokka.db.gatekeeper;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.injector.ControllerFactory;

@Singleton
public class GatekeeperService {

    private final LanguageManager languageManager;
    private final ControllerFactory controllerFactory;

    @Inject
    public GatekeeperService(LanguageManager languageManager, ControllerFactory controllerFactory) {
        this.languageManager = languageManager;
        this.controllerFactory = controllerFactory;
    }

    public void show() {
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/fxml/GatekeeperView.fxml"));
            viewLoader.setControllerFactory(controllerFactory::create);
            viewLoader.setResources(languageManager.getResourceBundle());

            Stage stage = new Stage();
            stage.setTitle("MOKKA Budget – " + getVersion());
            stage.setScene(new Scene(viewLoader.load()));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/general-styles.css").toExternalForm());
            stage.setWidth(500);
            stage.setOnHidden(event -> {
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
}
