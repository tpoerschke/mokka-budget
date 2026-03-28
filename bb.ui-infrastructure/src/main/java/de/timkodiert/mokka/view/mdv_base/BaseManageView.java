package de.timkodiert.mokka.view.mdv_base;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.view.View;

public abstract class BaseManageView<B> implements View, Initializable {

    @FXML
    protected Pane detailViewContainer;

    private final FXMLLoader fxmlLoader;
    private final LanguageManager languageManager;

    protected EntityBaseDetailView<B> detailView;

    protected BaseManageView(FXMLLoader fxmlLoader, LanguageManager languageManager) {
        this.fxmlLoader = fxmlLoader;
        this.languageManager = languageManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reloadTable(null);

        fxmlLoader.setLocation(getClass().getResource(getDetailViewFxmlLocation()));
        try {
            detailViewContainer.getChildren().add(fxmlLoader.load());
        } catch (Exception e) {
            StackTraceAlert.createAndLog(languageManager.get("alert.viewCouldNotBeOpened"), e).showAndWait();
            return;
        }

        detailView = fxmlLoader.getController();
        detailView.setOnUpdate(this::reloadTable);

        initControls();
    }

    public abstract void displayEntityById(int id);

    protected void displayNewEntity() {
        detailView.setBean(createEmptyEntity());
    }

    protected abstract void initControls();

    protected abstract B createEmptyEntity();

    protected abstract void reloadTable(@Nullable B updatedBean);

    protected abstract String getDetailViewFxmlLocation();
}
