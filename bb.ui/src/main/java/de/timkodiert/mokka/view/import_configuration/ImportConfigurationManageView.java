package de.timkodiert.mokka.view.import_configuration;

import java.util.Optional;
import javax.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.domain.ImportConfigurationCrudService;
import de.timkodiert.mokka.domain.ImportConfigurationDTO;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.view.FxmlResource;
import de.timkodiert.mokka.view.mdv_base.BaseListManageView;

public class ImportConfigurationManageView extends BaseListManageView<ImportConfigurationDTO> {

    @FXML
    private TableColumn<ImportConfigurationDTO, String> nameColumn;

    private final ImportConfigurationCrudService crudService;

    @Inject
    public ImportConfigurationManageView(FXMLLoader fxmlLoader,
                                         DialogFactory dialogFactory,
                                         LanguageManager languageManager,
                                         ImportConfigurationCrudService crudService) {
        super(fxmlLoader, dialogFactory, languageManager);
        this.crudService = crudService;
    }

    @Override
    public void displayEntityById(int id) {
        detailView.setBean(crudService.readById(id));
    }

    @Override
    protected void initControls() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    @Override
    protected ImportConfigurationDTO createEmptyEntity() {
        return new ImportConfigurationDTO();
    }

    @Override
    protected void reloadTable(@Nullable ImportConfigurationDTO updatedBean) {
        entityTable.getItems().setAll(crudService.readAll());
    }

    @FXML
    private void openNewImportConfiguration(ActionEvent actionEvent) {
        entityTable.getSelectionModel().clearSelection();
        lastSelectedRow = null;
        displayNewEntity();
    }

    @Override
    protected String getDetailViewFxmlLocation() {
        return FxmlResource.IMPORT_CONFIGURATION_DETAIL_VIEW.getPath();
    }

    @Override
    protected ImportConfigurationDTO discardChanges(ImportConfigurationDTO beanToDiscard) {
        return Optional.ofNullable(crudService.readById(beanToDiscard.getId())).orElseGet(this::createEmptyEntity);
    }
}
