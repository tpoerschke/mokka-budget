package de.timkodiert.mokka.view.unique_turnover;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.SerializationUtils;

import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.domain.CategoryCrudService;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.TurnoverDirection;
import de.timkodiert.mokka.domain.UniqueTurnoverCrudService;
import de.timkodiert.mokka.domain.UniqueTurnoverInformationDTO;
import de.timkodiert.mokka.ui.control.AutoCompleteTextField;
import de.timkodiert.mokka.ui.control.MoneyTextField;
import de.timkodiert.mokka.ui.helper.Bind;
import de.timkodiert.mokka.validation.ValidationResult;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.BaseDetailView;

public class UniqueExpenseInformationDetailView extends BaseDetailView<UniqueTurnoverInformationDTO> implements Initializable {

    @FXML
    private AutoCompleteTextField positionTextField;
    @FXML
    private MoneyTextField valueTextField;
    @FXML
    private ComboBox<TurnoverDirection> directionComboBox;
    @FXML
    private ComboBox<Reference<CategoryDTO>> categoryComboBox;

    private Stage stage;
    private UniqueTurnoverInformationDTO backupBean;

    private final UniqueTurnoverCrudService uniqueTurnoverCrudService;
    private final CategoryCrudService categoryCrudService;
    private final BiConsumer<UniqueTurnoverInformationDTO, UniqueTurnoverInformationDTO> updateCallback;

    @AssistedInject
    public UniqueExpenseInformationDetailView(ValidationWrapperFactory<UniqueTurnoverInformationDTO> validationWrapperFactory,
                                              UniqueTurnoverCrudService uniqueTurnoverCrudService,
                                              CategoryCrudService categoryCrudService,
                                              @Assisted BiConsumer<UniqueTurnoverInformationDTO, UniqueTurnoverInformationDTO> updateCallback) {
        super(validationWrapperFactory);
        this.uniqueTurnoverCrudService = uniqueTurnoverCrudService;
        this.categoryCrudService = categoryCrudService;
        this.updateCallback = updateCallback;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Reference<CategoryDTO>> categories = categoryCrudService.readAll().stream().map(c -> new Reference<>(CategoryDTO.class, c.getId(), c.getName())).toList();
        positionTextField.getAvailableEntries().addAll(uniqueTurnoverCrudService.getUniqueTurnoverInformationLabels());
        directionComboBox.getItems().setAll(TurnoverDirection.values());
        directionComboBox.setConverter(Converters.get(TurnoverDirection.class));

        // Bindings
        positionTextField.textProperty().bindBidirectional(beanAdapter.getProperty(UniqueTurnoverInformationDTO::getLabel, UniqueTurnoverInformationDTO::setLabel));
        valueTextField.integerValueProperty().bindBidirectional(beanAdapter.getProperty(UniqueTurnoverInformationDTO::getValue, UniqueTurnoverInformationDTO::setValue));
        Bind.comboBox(directionComboBox,
                      beanAdapter.getProperty(UniqueTurnoverInformationDTO::getDirection, UniqueTurnoverInformationDTO::setDirection),
                      Arrays.asList(TurnoverDirection.values()),
                      TurnoverDirection.class);
        Bind.comboBoxNullable(categoryComboBox,
                              beanAdapter.getProperty(UniqueTurnoverInformationDTO::getCategory, UniqueTurnoverInformationDTO::setCategory),
                              categories);

        // Validierungen
        validationMap.put("label", positionTextField);
        validationMap.put("direction", directionComboBox);
        validationWrapper.register(positionTextField.textProperty(), directionComboBox.getSelectionModel().selectedItemProperty());
        validationWrapper.registerCustomValidation("amountValid",
                                                   valueTextField.getTextField(),
                                                   () -> valueTextField.isStringFormatValid()
                                                           ? ValidationResult.valid()
                                                           : ValidationResult.error("{amount.format.valid}"),
                                                   valueTextField.getTextField().textProperty());
    }

    @Override
    protected void beanSet() {
        backupBean = SerializationUtils.clone(beanAdapter.getBean());
    }

    @Override
    protected UniqueTurnoverInformationDTO createEmptyEntity() {
        return new UniqueTurnoverInformationDTO();
    }

    @FXML
    private void onSave(ActionEvent e) {
        if (!validate()) {
            return;
        }
        updateCallback.accept(null, beanAdapter.getBean());
        stage.close();
    }

    @FXML
    private void onRevert() {
        UniqueTurnoverInformationDTO modified = beanAdapter.getBean();
        setBean(SerializationUtils.clone(backupBean));
        updateCallback.accept(modified, beanAdapter.getBean());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> onRevert());
    }
}
