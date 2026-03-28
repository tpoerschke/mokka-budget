package de.timkodiert.mokka.view.category;

import java.util.Optional;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.domain.CategoryCrudService;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.table.cell.ReferenceTableCell;
import de.timkodiert.mokka.view.FxmlResource;
import de.timkodiert.mokka.view.mdv_base.BaseListManageView;

public class CategoriesManageView extends BaseListManageView<CategoryDTO> {

    @FXML
    private TableColumn<CategoryDTO, String> nameColumn;
    @FXML
    private TableColumn<CategoryDTO, Reference<CategoryGroupDTO>> groupColumn;

    private final CategoryCrudService crudService;

    @Inject
    public CategoriesManageView(FXMLLoader fxmlLoader, LanguageManager languageManager, DialogFactory dialogFactory, CategoryCrudService crudService) {
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
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));
        groupColumn.setCellFactory(col -> new ReferenceTableCell<>());
    }

    @Override
    protected CategoryDTO createEmptyEntity() {
        return new CategoryDTO();
    }

    @Override
    protected void reloadTable(@Nullable CategoryDTO updatedBean) {
        entityTable.getItems().setAll(crudService.readAll());
    }

    @Override
    protected String getDetailViewFxmlLocation() {
        return FxmlResource.CATEGORY_DETAIL_VIEW.getPath();
    }

    @FXML
    private void openNewCategory() {
        entityTable.getSelectionModel().clearSelection();
        lastSelectedRow = null;
        displayNewEntity();
    }

    @Override
    protected CategoryDTO discardChanges(CategoryDTO beanToDiscard) {
        return Optional.ofNullable(crudService.readById(beanToDiscard.getId())).orElseGet(this::createEmptyEntity);
    }
}
