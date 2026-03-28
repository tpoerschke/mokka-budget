package de.timkodiert.mokka;

import atlantafx.base.theme.Theme;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.db.MigrationService;
import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.injector.DaggerViewComponent;
import de.timkodiert.mokka.injector.ViewComponent;
import de.timkodiert.mokka.properties.OperationMode;
import de.timkodiert.mokka.properties.PropertiesServiceImpl;

import static de.timkodiert.mokka.Constants.OPERATION_MODE_ARGUMENT_NAME;
import static de.timkodiert.mokka.injector.AppModule.with;

public class Main extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewComponent viewComponent = DaggerViewComponent.builder().appModule(with(getHostServices())).build();

        PropertiesServiceImpl propsService = viewComponent.getPropertiesService();
        Parameters params = getParameters();
        if (params.getNamed().containsKey(OPERATION_MODE_ARGUMENT_NAME)) {
            OperationMode operationMode = OperationMode.valueOf(params.getNamed().get(OPERATION_MODE_ARGUMENT_NAME));
            propsService.setOperationMode(operationMode);
        }
        try {
            propsService.load();
        } catch (Exception e) {
            StackTraceAlert.createAndLog("Error loading properties file!", e).showAndWait();
            Platform.exit();
        }

        Thread.setDefaultUncaughtExceptionHandler(viewComponent.getUncaughtExceptionHandler());

        Class<? extends Theme> theme = propsService.getTheme();
        Application.setUserAgentStylesheet(theme.getConstructor().newInstance().getUserAgentStylesheet());

        Converters converters = viewComponent.getConverters();
        converters.register();

        // Migration & Programmstart
        MigrationService migrationService = viewComponent.getMigrationService();
        if (migrationService.hasPendingMigrations()) {
            migrationService.show();
        }
        LOG.info("Showing MainView");
        viewComponent.getMainView().setAndShowPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {
        LOG.info("Starting Application - Version {}", Main.class.getPackage().getImplementationVersion());
        launch(args);
    }
}
