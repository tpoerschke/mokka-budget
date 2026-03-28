package de.timkodiert.mokka.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.domain.util.EntityManager;
import de.timkodiert.mokka.exception.TechnicalException;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.injector.ControllerFactory;
import de.timkodiert.mokka.properties.PropertiesService;
import de.timkodiert.mokka.util.StageBuilder;
import de.timkodiert.mokka.view.importer.ImportView;

import static de.timkodiert.mokka.properties.PropertiesService.USE_SYSTEM_MENU_BAR;
import static de.timkodiert.mokka.view.FxmlResource.ANALYSIS_VIEW;
import static de.timkodiert.mokka.view.FxmlResource.ANNUAL_OVERVIEW;
import static de.timkodiert.mokka.view.FxmlResource.IMPORT_VIEW;
import static de.timkodiert.mokka.view.FxmlResource.MAIN_VIEW;
import static de.timkodiert.mokka.view.FxmlResource.MANAGE_REGULAR_TURNOVER_VIEW;
import static de.timkodiert.mokka.view.FxmlResource.MANAGE_UNIQUE_TURNOVER_VIEW;
import static de.timkodiert.mokka.view.FxmlResource.MONTHLY_OVERVIEW;

@Singleton
public class MainViewImpl implements Initializable, MainView {

    private static final Logger LOG = LoggerFactory.getLogger(MainViewImpl.class);

    private Stage primaryStage;

    @FXML
    private BorderPane root;
    @FXML
    private MenuBar menuBar;

    private final ControllerFactory controllerFactory;
    private final EntityManager entityManager;
    private final PropertiesService propertiesService;
    private final LanguageManager languageManager;
    private final Provider<StageBuilder> stageBuilderProvider;

    @Inject
    public MainViewImpl(ControllerFactory controllerFactory,
                        EntityManager entityManager,
                        PropertiesService propertiesService,
                        LanguageManager languageManager,
                        Provider<StageBuilder> stageBuilderProvider) {
        this.controllerFactory = controllerFactory;
        this.entityManager = entityManager;
        this.propertiesService = propertiesService;
        this.languageManager = languageManager;
        this.stageBuilderProvider = stageBuilderProvider;
    }

    public void setAndShowPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(windowEvent -> {
            entityManager.closeSession();
            LOG.info("Stopping Application");
        });

        try {
            FXMLLoader templateLoader = new FXMLLoader();
            templateLoader.setResources(languageManager.getResourceBundle());
            templateLoader.setLocation(getClass().getResource(MAIN_VIEW.toString()));
            templateLoader.setController(this);
            Scene scene = new Scene(templateLoader.load());
            scene.getStylesheets().add(getClass().getResource("/css/general-styles.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/css/validation.css").toExternalForm());
            this.primaryStage.setScene(scene);
            this.primaryStage.setMaximized(true);
            this.primaryStage.show();
        } catch (Exception e) {
            StackTraceAlert.createAndLog(languageManager.get("alert.mainViewCouldNotBeOpened"), e).showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boolean useSystemMenuBar = "true".equals(propertiesService.getProperties().getProperty(USE_SYSTEM_MENU_BAR, "false"));
        menuBar.useSystemMenuBarProperty().set(useSystemMenuBar);

        // Das Kind laden (default)
        loadViewPartial(MONTHLY_OVERVIEW);
    }

    private String getVersion() {
        return "Version " + getClass().getPackage().getImplementationVersion();
    }

    @Override
    public @Nullable View loadViewPartial(FxmlResource resource) {
        try {
            FXMLLoader templateLoader = new FXMLLoader();
            templateLoader.setLocation(getClass().getResource(resource.getPath()));
            templateLoader.setControllerFactory(controllerFactory::create);
            templateLoader.setResources(languageManager.getResourceBundle());
            this.root.setCenter(templateLoader.load());
            this.primaryStage.setTitle(String.format("%s – MOKKA Budget – %s", languageManager.get(resource.getStageTitle()), getVersion()));
            return templateLoader.getController();
        } catch (Exception e) {
            // Könnte in den UncaughtExceptionHandler verlagert werden
            StackTraceAlert.createAndLog(languageManager.get("alert.viewCouldNotBeOpened"), e).showAndWait();
        }
        return null;
    }

    @FXML
    public void showMonthlyOverview(ActionEvent event) {
        loadViewPartial(MONTHLY_OVERVIEW);
    }

    @FXML
    public void showAnnualOverview(ActionEvent event) {
        loadViewPartial(ANNUAL_OVERVIEW);
    }

    @FXML
    private void showAnalysisView() {
        loadViewPartial(ANALYSIS_VIEW);
    }

    @FXML
    private void openManageExpensesView(ActionEvent event) {
        loadViewPartial(MANAGE_REGULAR_TURNOVER_VIEW);
    }

    @FXML
    private void openUniqueExpensesManageView(ActionEvent event) {
        loadViewPartial(MANAGE_UNIQUE_TURNOVER_VIEW);
    }

    @FXML
    public void openImportView(ActionEvent event) {
        View view = loadViewPartial(IMPORT_VIEW);
        if (view instanceof ImportView importView) {
            importView.setMainView(this);
        }
    }

    @FXML
    private void openManageCategoriesView(ActionEvent event) {
        loadViewPartial(FxmlResource.MANAGE_CATEGORY_VIEW);
    }

    @FXML
    private void openManageCategoryGroupsView(ActionEvent event) {
        loadViewPartial(FxmlResource.CATEGORY_GROUP_MANAGE_VIEW);
    }

    @FXML
    private void openBillingsManageView(ActionEvent event) {
        loadViewPartial(FxmlResource.BILLING_MANAGE_VIEW);
    }

    @FXML
    private void openSettingsView() {
        propertiesService.buildWindow().showAndWait();
    }

    @FXML
    private void openAboutView() {
        try {
            stageBuilderProvider.get()
                                .withFXMLResource(FxmlResource.ABOUT_VIEW.getPath())
                                .withOwner(primaryStage)
                                .withModality(Modality.APPLICATION_MODAL)
                                .withTitle(languageManager.get(FxmlResource.ABOUT_VIEW.getStageTitle()))
                                .minSize(700, 800)
                                .build()
                                .stage()
                                .showAndWait();
        } catch (IOException e) {
            throw TechnicalException.forFxmlNotFound(e);
        }
    }


    @FXML
    private void onDragOver(DragEvent e) {
        Dragboard dragboard = e.getDragboard();
        if (dragboard.hasFiles() && dragboard.getFiles().stream().anyMatch(this::isCsvFile)) {
            e.acceptTransferModes(TransferMode.COPY);
        }
        e.consume();
    }

    private boolean isCsvFile(File file) {
        String name = file.getName();
        return "csv".equals(name.substring(name.lastIndexOf('.') + 1));
    }

    @FXML
    private void onDragDropped(DragEvent e) {
        View view = loadViewPartial(IMPORT_VIEW);
        if (view instanceof ImportView importView) {
            importView.setMainView(this);
            e.getDragboard().getFiles().stream().filter(this::isCsvFile).findFirst().ifPresent(importView.selectedFileProperty()::set);
        }
    }
}
