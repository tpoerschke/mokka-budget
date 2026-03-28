package de.timkodiert.mokka.db;

import java.util.Set;

import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.injector.ControllerFactory;
import de.timkodiert.mokka.properties.PropertiesService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MigrationService {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationService.class);

    private final LanguageManager languageManager;
    private final ControllerFactory controllerFactory;

    private final Flyway flyway;

    @Getter
    private int pendingCount = 0;
    private final StringProperty currentScript = new SimpleStringProperty();
    private final IntegerProperty numMigrated = new SimpleIntegerProperty();
    private final BooleanProperty migrationFinished = new SimpleBooleanProperty();
    private final BooleanProperty migrationError = new SimpleBooleanProperty();

    @Inject
    public MigrationService(LanguageManager languageManager, PropertiesService propertiesService, ControllerFactory controllerFactory) {
        this.languageManager = languageManager;
        this.controllerFactory = controllerFactory;
        flyway = Flyway.configure()
                       .dataSource(propertiesService.getDbPath(), "bb", "")
                       .callbacks(new NotifierCallback())
                       .load();
        pendingCount = flyway.info().pending().length;
    }

    public boolean hasPendingMigrations() {
        return flyway.info().pending().length > 0;
    }

    public void migrate() {
        flyway.migrate();
    }

    public void show() {
        try {
            FXMLLoader viewLoader = new FXMLLoader();
            viewLoader.setLocation(getClass().getResource("/fxml/MigrationView.fxml"));
            viewLoader.setControllerFactory(controllerFactory::create);
            viewLoader.setResources(languageManager.getResourceBundle());

            Stage stage = new Stage();
            stage.setTitle("Migration – MOKKA Budget");
            stage.setScene(new Scene(viewLoader.load()));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/general-styles.css").toExternalForm());
            stage.setWidth(600);
            stage.setOnHidden(event -> {
                if (!migrationFinishedProperty().get()) {
                    Platform.exit();
                    System.exit(1);
                }
            });
            stage.showAndWait();
        } catch (Exception e) {
            StackTraceAlert.createAndLog(languageManager.get("alert.viewCouldNotBeOpened"), e).showAndWait();
        }
    }

    public StringProperty currentScriptProperty() {
        return currentScript;
    }

    public IntegerProperty numMigratedProperty() {
        return numMigrated;
    }

    public BooleanProperty migrationFinishedProperty() {
        return migrationFinished;
    }

    public BooleanProperty migrationErrorProperty() {
        return migrationError;
    }

    private class NotifierCallback implements Callback {

        @Override
        public boolean supports(Event event, Context context) {
            return Set.of(Event.BEFORE_EACH_MIGRATE, Event.AFTER_EACH_MIGRATE, Event.AFTER_MIGRATE_ERROR, Event.AFTER_MIGRATE_OPERATION_FINISH).contains(event);
        }

        @Override
        public boolean canHandleInTransaction(Event event, Context context) {
            return true; // Irrelevant, da nicht mit der Database interagiert wird
        }

        @Override
        public void handle(Event event, Context context) {
            switch (event) {
                case BEFORE_EACH_MIGRATE -> {
                    LOG.info("Migrate with {}", context.getMigrationInfo().getScript());
                    currentScript.set(context.getMigrationInfo().getScript());
                }
                case AFTER_EACH_MIGRATE -> numMigrated.set(numMigrated.get() + 1);
                case AFTER_MIGRATE_ERROR -> {
                    LOG.error("Migrate with {} failed", context.getMigrationInfo().getScript());
                    migrationError.set(true);
                }
                case AFTER_MIGRATE_OPERATION_FINISH -> migrationFinished.set(true);
                default -> {
                    // Andere Events sind nicht relevant
                }
            }
        }

        @Override
        public String getCallbackName() {
            return NotifierCallback.class.getSimpleName();
        }
    }
}
