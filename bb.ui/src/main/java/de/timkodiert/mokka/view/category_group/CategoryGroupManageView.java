package de.timkodiert.mokka.view.category_group;

import java.util.Optional;
import javax.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.domain.CategoryGroupCrudService;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.view.FxmlResource;
import de.timkodiert.mokka.view.mdv_base.BaseListManageView;

public class CategoryGroupManageView extends BaseListManageView<CategoryGroupDTO> {

    @FXML
    private TableColumn<CategoryGroupDTO, String> nameColumn;

    private final CategoryGroupCrudService crudService;

    @Inject
    public CategoryGroupManageView(FXMLLoader fxmlLoader, DialogFactory dialogFactory, LanguageManager languageManager, CategoryGroupCrudService crudService) {
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
    protected CategoryGroupDTO createEmptyEntity() {
        return new CategoryGroupDTO();
    }

    @Override
    protected void reloadTable(@Nullable CategoryGroupDTO updatedBean) {
        entityTable.getItems().setAll(crudService.readAll());
    }

    @FXML
    private void openNewCategoryGroup(ActionEvent actionEvent) {
        entityTable.getSelectionModel().clearSelection();
        lastSelectedRow = null;
        displayNewEntity();
    }

    @Override
    protected String getDetailViewFxmlLocation() {
        return FxmlResource.CATEGORY_GROUP_DETAIL_VIEW.getPath();
    }

    @Override
    protected CategoryGroupDTO discardChanges(CategoryGroupDTO beanToDiscard) {
        return Optional.ofNullable(crudService.readById(beanToDiscard.getId())).orElseGet(this::createEmptyEntity);
    }
}
