package de.timkodiert.mokka.view.import_configuration;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import de.timkodiert.mokka.domain.ImportConfigurationCrudService;
import de.timkodiert.mokka.domain.ImportConfigurationDTO;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.importer.CsvEncoding;
import de.timkodiert.mokka.ui.helper.Bind;
import de.timkodiert.mokka.validation.ValidationResult;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.EntityBaseDetailView;

public class ImportConfigurationDetailView extends EntityBaseDetailView<ImportConfigurationDTO> implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private TextField nameTextField;
    @FXML
    private Spinner<Integer> skipLinesTextField;
    @FXML
    private ComboBox<CsvEncoding> encodingComboBox;
    @FXML
    private CheckBox defaultCheckBox;
    @FXML
    private Spinner<Integer> columnDateSpinner;
    @FXML
    private Spinner<Integer> columnReceiverSpinner;
    @FXML
    private Spinner<Integer> columnPostingTextSpinner;
    @FXML
    private Spinner<Integer> columnReferenceSpinner;
    @FXML
    private Spinner<Integer> columnAmountSpinner;

    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;

    private final LanguageManager languageManager;
    private final ImportConfigurationCrudService crudService;

    @Inject
    public ImportConfigurationDetailView(ValidationWrapperFactory<ImportConfigurationDTO> validationWrapperFactory,
                                         LanguageManager languageManager,
                                         ImportConfigurationCrudService crudService) {
        super(validationWrapperFactory);
        this.languageManager = languageManager;
        this.crudService = crudService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.disableProperty().bind(beanAdapter.isEmpty());
        saveButton.disableProperty().bind(beanAdapter.dirty().not());
        discardButton.disableProperty().bind(beanAdapter.dirty().not());

        nameTextField.textProperty().bindBidirectional(beanAdapter.getProperty(ImportConfigurationDTO::getName, ImportConfigurationDTO::setName));
        Bind.comboBox(encodingComboBox,
                      beanAdapter.getProperty(ImportConfigurationDTO::getEncoding, ImportConfigurationDTO::setEncoding),
                      List.of(CsvEncoding.values()),
                      CsvEncoding.class);
        defaultCheckBox.selectedProperty().bindBidirectional(beanAdapter.getProperty(ImportConfigurationDTO::isDefault, ImportConfigurationDTO::setDefault));

        Bind.spinner(skipLinesTextField, beanAdapter.getProperty(ImportConfigurationDTO::getSkipLines, ImportConfigurationDTO::setSkipLines));
        Bind.spinner(columnDateSpinner, beanAdapter.getProperty(ImportConfigurationDTO::getColumnDate, ImportConfigurationDTO::setColumnDate));
        Bind.spinner(columnReceiverSpinner, beanAdapter.getProperty(ImportConfigurationDTO::getColumnReceiver, ImportConfigurationDTO::setColumnReceiver));
        Bind.spinner(columnPostingTextSpinner, beanAdapter.getProperty(ImportConfigurationDTO::getColumnPostingText, ImportConfigurationDTO::setColumnPostingText));
        Bind.spinner(columnReferenceSpinner, beanAdapter.getProperty(ImportConfigurationDTO::getColumnReference, ImportConfigurationDTO::setColumnReference));
        Bind.spinner(columnAmountSpinner, beanAdapter.getProperty(ImportConfigurationDTO::getColumnAmount, ImportConfigurationDTO::setColumnAmount));
        unbindSpinnerTooltips();

        validationMap.put("name", nameTextField);
        validationMap.put("encoding", encodingComboBox);
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getName, ImportConfigurationDTO::setName));
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getEncoding, ImportConfigurationDTO::setEncoding));
        validateNotSameColumnMappingValue("columnMappingValuesDate", columnDateSpinner);
        validateNotSameColumnMappingValue("columnMappingValuesReceiver", columnReceiverSpinner);
        validateNotSameColumnMappingValue("columnMappingValuesPostingText", columnPostingTextSpinner);
        validateNotSameColumnMappingValue("columnMappingValuesReference", columnReferenceSpinner);
        validateNotSameColumnMappingValue("columnMappingValuesAmount", columnAmountSpinner);
        validationWrapper.registerCustomValidation("defaultUnique",
                                                   defaultCheckBox,
                                                   () -> defaultConfigIsUnique()
                                                           ? ValidationResult.valid()
                                                           : ValidationResult.error(languageManager.get("ImportConfigurationDV.validation.defaultAlreadyExists")),
                                                   defaultCheckBox.selectedProperty());
    }

    private void unbindSpinnerTooltips() {
        columnDateSpinner.getEditor().tooltipProperty().unbind();
        columnReceiverSpinner.getEditor().tooltipProperty().unbind();
        columnPostingTextSpinner.getEditor().tooltipProperty().unbind();
        columnReferenceSpinner.getEditor().tooltipProperty().unbind();
        columnAmountSpinner.getEditor().tooltipProperty().unbind();
    }

    private void validateNotSameColumnMappingValue(String name, Spinner<Integer> spinner) {
        validationWrapper.registerCustomValidation(name,
                                                   spinner.getEditor(),
                                                   () -> fiveDistinctColumnMappingValues()
                                                           ? ValidationResult.valid()
                                                           : ValidationResult.error(languageManager.get("ImportConfigurationDV.validation.columnMappingsNotDistinct")),
                                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnDate, ImportConfigurationDTO::setColumnDate),
                                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnReceiver, ImportConfigurationDTO::setColumnReceiver),
                                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnPostingText, ImportConfigurationDTO::setColumnPostingText),
                                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnReference, ImportConfigurationDTO::setColumnReference),
                                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnAmount, ImportConfigurationDTO::setColumnAmount));
    }

    private boolean fiveDistinctColumnMappingValues() {
        ImportConfigurationDTO bean = getBean();
        if (bean == null) {
            return true;
        }
        return Stream.of(bean.getColumnDate(),
                         bean.getColumnReceiver(),
                         bean.getColumnPostingText(),
                         bean.getColumnReference(),
                         bean.getColumnAmount()).distinct().count() == 5;
    }

    private boolean defaultConfigIsUnique() {
        ImportConfigurationDTO bean = getBean();
        if (bean == null || !bean.isDefault()) {
            return true;
        }
        return crudService.readAll().stream().noneMatch(config -> config.isDefault() && config.getId() != bean.getId());
    }

    @Override
    protected ImportConfigurationDTO createEmptyEntity() {
        return new ImportConfigurationDTO();
    }

    @Override
    public boolean save() {
        ImportConfigurationDTO bean = getBean();
        if (bean == null) {
            return false;
        }
        Predicate<ImportConfigurationDTO> servicePersistMethod = bean.isNew() ? crudService::create : crudService::update;
        boolean success = validate() && servicePersistMethod.test(bean);
        if (success) {
            beanAdapter.setDirty(false);
            onUpdate.accept(bean);
            return true;
        }
        return false;
    }

    @Override
    protected ImportConfigurationDTO discardChanges() {
        return Optional.ofNullable(crudService.readById(Objects.requireNonNull(getBean()).getId())).orElseGet(this::createEmptyEntity);
    }

    @FXML
    private void delete(ActionEvent event) {
        ImportConfigurationDTO importConfigurationDTO = getBean();
        crudService.delete(importConfigurationDTO.getId());
        beanAdapter.setBean(null);
        onUpdate.accept(null);
    }
}
