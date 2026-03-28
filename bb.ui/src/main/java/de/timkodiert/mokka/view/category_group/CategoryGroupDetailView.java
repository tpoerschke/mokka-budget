package de.timkodiert.mokka.view.category_group;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import de.timkodiert.mokka.domain.CategoryGroupCrudService;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.EntityBaseDetailView;

public class CategoryGroupDetailView extends EntityBaseDetailView<CategoryGroupDTO> implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;

    private final CategoryGroupCrudService crudService;

    @Inject
    public CategoryGroupDetailView(ValidationWrapperFactory<CategoryGroupDTO> validationWrapperFactory, CategoryGroupCrudService crudService) {
        super(validationWrapperFactory);
        this.crudService = crudService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.disableProperty().bind(beanAdapter.isEmpty());
        saveButton.disableProperty().bind(beanAdapter.dirty().not());
        discardButton.disableProperty().bind(beanAdapter.dirty().not());

        nameTextField.textProperty().bindBidirectional(beanAdapter.getProperty(CategoryGroupDTO::getName, CategoryGroupDTO::setName));
        descriptionTextArea.textProperty().bindBidirectional(beanAdapter.getProperty(CategoryGroupDTO::getDescription, CategoryGroupDTO::setDescription));

        // Validierungen
        validationMap.put("name", nameTextField);
        validationWrapper.register(beanAdapter.getProperty(CategoryGroupDTO::getName, CategoryGroupDTO::setName));
    }

    @Override
    protected CategoryGroupDTO createEmptyEntity() {
        return new CategoryGroupDTO();
    }

    @Override
    public boolean save() {
        CategoryGroupDTO bean = getBean();
        if (bean == null) {
            return false;
        }
        Predicate<CategoryGroupDTO> servicePersistMethod = bean.isNew() ? crudService::create : crudService::update;
        boolean success = validate() && servicePersistMethod.test(bean);
        if (success) {
            beanAdapter.setDirty(false);
            onUpdate.accept(bean);
            return true;
        }
        return false;
    }

    @Override
    protected CategoryGroupDTO discardChanges() {
        return Optional.ofNullable(crudService.readById(Objects.requireNonNull(getBean()).getId())).orElseGet(this::createEmptyEntity);
    }

    @FXML
    private void delete(ActionEvent event) {
        CategoryGroupDTO categoryGroupDTO = this.getBean();
        crudService.delete(categoryGroupDTO.getId());
        beanAdapter.setBean(null);
        onUpdate.accept(null);
    }
}
