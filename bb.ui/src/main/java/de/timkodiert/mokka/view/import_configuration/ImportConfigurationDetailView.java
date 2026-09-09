package de.timkodiert.mokka.view.import_configuration;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.IntegerStringConverter;

import de.timkodiert.mokka.domain.ImportConfigurationCrudService;
import de.timkodiert.mokka.domain.ImportConfigurationDTO;
import de.timkodiert.mokka.importer.CsvEncoding;
import de.timkodiert.mokka.ui.helper.Bind;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.EntityBaseDetailView;

public class ImportConfigurationDetailView extends EntityBaseDetailView<ImportConfigurationDTO> implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField skipLinesTextField;
    @FXML
    private ComboBox<CsvEncoding> encodingComboBox;
    @FXML
    private TextField columnDateTextField;
    @FXML
    private TextField columnReceiverTextField;
    @FXML
    private TextField columnPostingTextTextField;
    @FXML
    private TextField columnReferenceTextField;
    @FXML
    private TextField columnAmountTextField;

    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;

    private final ImportConfigurationCrudService crudService;

    @Inject
    public ImportConfigurationDetailView(ValidationWrapperFactory<ImportConfigurationDTO> validationWrapperFactory,
                                         ImportConfigurationCrudService crudService) {
        super(validationWrapperFactory);
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

        Bindings.bindBidirectional(skipLinesTextField.textProperty(),
                                   beanAdapter.getProperty(ImportConfigurationDTO::getSkipLines, ImportConfigurationDTO::setSkipLines),
                                   new IntegerStringConverter());
        Bindings.bindBidirectional(columnDateTextField.textProperty(),
                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnDate, ImportConfigurationDTO::setColumnDate),
                                   new IntegerStringConverter());
        Bindings.bindBidirectional(columnReceiverTextField.textProperty(),
                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnReceiver, ImportConfigurationDTO::setColumnReceiver),
                                   new IntegerStringConverter());
        Bindings.bindBidirectional(columnPostingTextTextField.textProperty(),
                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnPostingText, ImportConfigurationDTO::setColumnPostingText),
                                   new IntegerStringConverter());
        Bindings.bindBidirectional(columnReferenceTextField.textProperty(),
                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnReference, ImportConfigurationDTO::setColumnReference),
                                   new IntegerStringConverter());
        Bindings.bindBidirectional(columnAmountTextField.textProperty(),
                                   beanAdapter.getProperty(ImportConfigurationDTO::getColumnAmount, ImportConfigurationDTO::setColumnAmount),
                                   new IntegerStringConverter());

        validationMap.put("name", nameTextField);
        validationMap.put("skipLines", skipLinesTextField);
        validationMap.put("encoding", encodingComboBox);
        validationMap.put("columnDate", columnDateTextField);
        validationMap.put("columnReceiver", columnReceiverTextField);
        validationMap.put("columnPostingText", columnPostingTextTextField);
        validationMap.put("columnReference", columnReferenceTextField);
        validationMap.put("columnAmount", columnAmountTextField);
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getName, ImportConfigurationDTO::setName));
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getSkipLines, ImportConfigurationDTO::setSkipLines));
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getEncoding, ImportConfigurationDTO::setEncoding));
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getColumnDate, ImportConfigurationDTO::setColumnDate));
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getColumnReceiver, ImportConfigurationDTO::setColumnReceiver));
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getColumnPostingText, ImportConfigurationDTO::setColumnPostingText));
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getColumnReference, ImportConfigurationDTO::setColumnReference));
        validationWrapper.register(beanAdapter.getProperty(ImportConfigurationDTO::getColumnAmount, ImportConfigurationDTO::setColumnAmount));
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
