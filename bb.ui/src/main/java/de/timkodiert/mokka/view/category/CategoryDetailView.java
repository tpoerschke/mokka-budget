package de.timkodiert.mokka.view.category;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javax.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import de.timkodiert.mokka.budget.BudgetType;
import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.converter.ReferenceStringConverter;
import de.timkodiert.mokka.domain.CategoryCrudService;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.CategoryGroupCrudService;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.ui.control.MoneyTextField;
import de.timkodiert.mokka.ui.helper.Bind;
import de.timkodiert.mokka.validation.ValidationResult;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.EntityBaseDetailView;

public class CategoryDetailView extends EntityBaseDetailView<CategoryDTO> implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private ComboBox<Reference<CategoryGroupDTO>> groupComboBox;
    @FXML
    private MoneyTextField budgetValueTextField;
    @FXML
    private CheckBox budgetActiveCheckBox;
    @FXML
    private ComboBox<BudgetType> budgetTypeComboBox;

    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;

    private final LanguageManager languageManager;
    private final CategoryCrudService categoryCrudService;
    private final CategoryGroupCrudService categoryGroupCrudService;

    @Inject
    protected CategoryDetailView(ValidationWrapperFactory<CategoryDTO> validationWrapperFactory,
                                 LanguageManager languageManager,
                                 CategoryCrudService categoryCrudService,
                                 CategoryGroupCrudService categoryGroupCrudService) {
        super(validationWrapperFactory);
        this.languageManager = languageManager;
        this.categoryCrudService = categoryCrudService;
        this.categoryGroupCrudService = categoryGroupCrudService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.disableProperty().bind(beanAdapter.isEmpty());
        saveButton.disableProperty().bind(beanAdapter.dirty().not());
        discardButton.disableProperty().bind(beanAdapter.dirty().not());

        groupComboBox.setConverter(new ReferenceStringConverter<>());
        groupComboBox.getItems().add(null);
        groupComboBox.getItems().addAll(categoryGroupCrudService.readAll()
                                                                .stream()
                                                                .map(cg -> new Reference<>(CategoryGroupDTO.class, cg.getId(), cg.getName()))
                                                                .toList());
        budgetTypeComboBox.setConverter(Converters.get(BudgetType.class));
        budgetTypeComboBox.getItems().add(null);
        budgetTypeComboBox.getItems().addAll(BudgetType.values());

        // Name und Beschreibung
        nameTextField.textProperty().bindBidirectional(beanAdapter.getProperty(CategoryDTO::getName, CategoryDTO::setName));
        descriptionTextArea.textProperty().bindBidirectional(beanAdapter.getProperty(CategoryDTO::getDescription, CategoryDTO::setDescription));
        // Kategoriegruppe
        Bind.comboBox(groupComboBox, beanAdapter.getProperty(CategoryDTO::getGroup, CategoryDTO::setGroup));
        // Budget
        budgetActiveCheckBox.selectedProperty().bindBidirectional(beanAdapter.getProperty(CategoryDTO::isBudgetActive, CategoryDTO::setBudgetActive));
        budgetValueTextField.integerValueProperty().bindBidirectional(beanAdapter.getProperty(CategoryDTO::getBudgetValue, CategoryDTO::setBudgetValue));
        Bind.comboBox(budgetTypeComboBox, beanAdapter.getProperty(CategoryDTO::getBudgetType, CategoryDTO::setBudgetType));

        // Validierungen
        validationMap.put("name", nameTextField);
        validationWrapper.register(beanAdapter.getProperty(CategoryDTO::getName, CategoryDTO::setName));
        validationWrapper.registerCustomValidation("budgetValueValid",
                                                   budgetValueTextField.getTextField(),
                                                   () -> budgetValueTextField.isStringFormatValid()
                                                           ? ValidationResult.valid()
                                                           : ValidationResult.error("{amount.format.valid}"),
                                                   budgetValueTextField.getTextField().textProperty());
    }

    @Override
    protected CategoryDTO createEmptyEntity() {
        return new CategoryDTO();
    }

    @Override
    public boolean save() {
        CategoryDTO bean = getBean();
        if (bean == null) {
            return false;
        }

        Predicate<CategoryDTO> servicePersistMethod = bean.isNew() ? categoryCrudService::create : categoryCrudService::update;
        boolean success = validate() && servicePersistMethod.test(bean);
        //        boolean saved = super.save();
        //        CategoryGroup catGroup = entity.get().getGroup();
        //        if (saved && catGroup != null) {
        //            // TODO: Dafür eine Schnittstelle schaffen bzw. ins Repository schieben
        //            entityManager.refresh(catGroup);
        //            return true;
        //        }
        if (success) {
            beanAdapter.setDirty(false);
            onUpdate.accept(bean);
            return true;
        }
        return false;
    }

    @Override
    protected CategoryDTO discardChanges() {
        return Optional.ofNullable(categoryCrudService.readById(Objects.requireNonNull(getBean()).getId())).orElseGet(this::createEmptyEntity);
    }

    @FXML
    private void delete(ActionEvent event) {
        CategoryDTO category = this.getBean();
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Die Kategorie \"" + category.getName() + "\" wirklich löschen?",
                                            ButtonType.YES,
                                            ButtonType.NO);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.filter(ButtonType.NO::equals).isPresent()) {
            return;
        }

        if (category.isHasLinkedTurnover()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText(languageManager.get("manageCategories.alert.expensesAreAssignedToThisCategory"));

            if (alert.showAndWait().filter(ButtonType.CANCEL::equals).isPresent()) {
                return;
            }
        }

        categoryCrudService.delete(category.getId());
        setBean(null);
        onUpdate.accept(null);
    }
}
