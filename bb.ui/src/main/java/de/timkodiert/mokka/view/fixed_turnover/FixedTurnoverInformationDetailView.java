package de.timkodiert.mokka.view.fixed_turnover;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.commons.lang3.SerializationUtils;

import de.timkodiert.mokka.domain.PaymentInformationDTO;
import de.timkodiert.mokka.domain.PaymentType;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.ui.control.MoneyTextField;
import de.timkodiert.mokka.ui.control.MonthYearPicker;
import de.timkodiert.mokka.ui.helper.Bind;
import de.timkodiert.mokka.validation.ValidationResult;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.BaseDetailView;

public class FixedTurnoverInformationDetailView extends BaseDetailView<PaymentInformationDTO> implements Initializable {

    @FXML
    private MoneyTextField valueTextField;
    @FXML
    private Label month1Label;
    @FXML
    private Label month2Label;
    @FXML
    private Label month3Label;
    @FXML
    private Label month4Label;
    @FXML
    private ChoiceBox<String> month1ChoiceBox;
    @FXML
    private ChoiceBox<String> month2ChoiceBox;
    @FXML
    private ChoiceBox<String> month3ChoiceBox;
    @FXML
    private ChoiceBox<String> month4ChoiceBox;
    @FXML
    private ComboBox<PaymentType> typeChoiceBox;
    @FXML
    private MonthYearPicker startMonthWidget;
    @FXML
    private MonthYearPicker endMonthWidget;

    private Stage stage;
    private PaymentInformationDTO backupBean;

    private final LanguageManager languageManager;
    private final FXMLLoader fxmlLoader;
    private final BiConsumer<PaymentInformationDTO, PaymentInformationDTO> onSaveCallback;

    @AssistedInject
    public FixedTurnoverInformationDetailView(ValidationWrapperFactory<PaymentInformationDTO> validationWrapperFactory,
                                              LanguageManager languageManager,
                                              FXMLLoader fxmlLoader,
                                              @Assisted BiConsumer<PaymentInformationDTO, PaymentInformationDTO> updateCallback) {
        super(validationWrapperFactory);
        this.languageManager = languageManager;
        this.fxmlLoader = fxmlLoader;
        this.onSaveCallback = updateCallback;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startMonthWidget.init(fxmlLoader, languageManager);
        endMonthWidget.init(fxmlLoader, languageManager);

        typeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> typeChoiceBoxListener(newValue));

        List.of(month1ChoiceBox, month2ChoiceBox, month3ChoiceBox, month4ChoiceBox)
            .forEach(e -> e.getItems().addAll(FXCollections.observableArrayList(languageManager.getMonths())));

        // Bindings
        valueTextField.integerValueProperty().bindBidirectional(beanAdapter.getProperty(PaymentInformationDTO::getValue, PaymentInformationDTO::setValue));

        List<PaymentType> typeList = List.of(PaymentType.MONTHLY, PaymentType.ANNUAL, PaymentType.SEMIANNUAL, PaymentType.QUARTERLY);
        Bind.comboBox(typeChoiceBox,
                      beanAdapter.getProperty(PaymentInformationDTO::getType, PaymentInformationDTO::setType),
                      typeList,
                      PaymentType.class);

        startMonthWidget.valueProperty().bindBidirectional(beanAdapter.getProperty(PaymentInformationDTO::getStart, PaymentInformationDTO::setStart));
        endMonthWidget.valueProperty().bindBidirectional(beanAdapter.getProperty(PaymentInformationDTO::getEnd, PaymentInformationDTO::setEnd));

        // Validierungen
        initValidation();
    }

    private void initValidation() {
        validationMap.put("start", startMonthWidget.getMonthChoiceBox());
        validationMap.put("type", typeChoiceBox);
        validationWrapper.register(typeChoiceBox.getSelectionModel().selectedItemProperty(),
                                   startMonthWidget.valueProperty(),
                                   endMonthWidget.valueProperty());
        validationWrapper.registerCustomValidation("valueValid",
                                                   valueTextField.getTextField(),
                                                   () -> valueTextField.isStringFormatValid()
                                                           ? ValidationResult.valid()
                                                           : ValidationResult.error("{amount.format.valid}"),
                                                   valueTextField.getTextField().textProperty());
        initCustomValidationForMonthChoiceBox("month1Valid", month1ChoiceBox);
        initCustomValidationForMonthChoiceBox("month2Valid", month2ChoiceBox);
        initCustomValidationForMonthChoiceBox("month3Valid", month3ChoiceBox);
        initCustomValidationForMonthChoiceBox("month4Valid", month4ChoiceBox);
        validationWrapper.registerCustomValidation("endBeforeStart",
                                                   endMonthWidget.getMonthChoiceBox(),
                                                   () -> endMonthWidget.valueProperty().get() == null ||
                                                           endMonthWidget.valueProperty().get().isAfter(startMonthWidget.valueProperty().get())
                                                           ? ValidationResult.valid()
                                                           : ValidationResult.error("{fixedTurnover.end.afterStart}"),
                                                   month4ChoiceBox.getSelectionModel().selectedItemProperty(),
                                                   typeChoiceBox.getSelectionModel().selectedItemProperty());
    }

    private void initCustomValidationForMonthChoiceBox(String name, ChoiceBox<String> monthChoiceBox) {
        validationWrapper.registerCustomValidation(name,
                                                   monthChoiceBox,
                                                   () -> !monthChoiceBox.isVisible() || monthChoiceBox.getSelectionModel().getSelectedItem() != null
                                                           ? ValidationResult.valid()
                                                           : ValidationResult.error("{fixedTurnover.month.selected}"),
                                                   monthChoiceBox.getSelectionModel().selectedItemProperty(),
                                                   typeChoiceBox.getSelectionModel().selectedItemProperty());
    }

    @Override
    protected void beanSet() {
        backupBean = SerializationUtils.clone(beanAdapter.getBean());
        PaymentInformationDTO payInfo = beanAdapter.getBean();
        if (PaymentType.MONTHLY != payInfo.getType()) {
            List<ChoiceBox<String>> choiceBoxes = List.of(month1ChoiceBox, month2ChoiceBox, month3ChoiceBox, month4ChoiceBox);
            for (int i = 0; i < payInfo.getMonthsOfPayment().size(); i++) {
                choiceBoxes.get(i).getSelectionModel().select(payInfo.getMonthsOfPayment().get(i) - 1);
            }
        }
    }

    @FXML
    private void onSave(ActionEvent e) {
        if (!validate()) {
            return;
        }
        setMonthsOfPayment(beanAdapter.getBean());
        onSaveCallback.accept(null, beanAdapter.getBean());
        stage.close();
    }

    @FXML
    private void onRevert() {
        PaymentInformationDTO modified = beanAdapter.getBean();
        setBean(SerializationUtils.clone(backupBean));
        onSaveCallback.accept(modified, beanAdapter.getBean());
    }

    private void setMonthsOfPayment(PaymentInformationDTO paymentInformationDTO) {
        List<Integer> datesOfPayment = IntStream.rangeClosed(1, 12).boxed().toList();
        if (typeChoiceBox.getSelectionModel().getSelectedItem() != PaymentType.MONTHLY) {
            datesOfPayment = Stream.of(month1ChoiceBox, month2ChoiceBox, month3ChoiceBox, month4ChoiceBox)
                                   .map(box -> box.getSelectionModel().getSelectedIndex())
                                   .filter(selectedIndex -> selectedIndex > -1)
                                   .map(selectedIndex -> selectedIndex + 1)
                                   .toList();
        }
        paymentInformationDTO.setMonthsOfPayment(datesOfPayment);
    }

    private void typeChoiceBoxListener(PaymentType newValue) {
        switch (newValue) {
            case ANNUAL -> manageChoiceBoxes(List.of(month1Label, month1ChoiceBox),
                                             List.of(month2Label, month2ChoiceBox,
                                                     month3Label, month3ChoiceBox,
                                                     month4Label, month4ChoiceBox));
            case SEMIANNUAL -> manageChoiceBoxes(List.of(month1Label, month1ChoiceBox,
                                                         month2Label, month2ChoiceBox),
                                                 List.of(month3Label, month3ChoiceBox,
                                                         month4Label, month4ChoiceBox));
            case MONTHLY -> manageChoiceBoxes(List.of(),
                                              List.of(month1Label, month1ChoiceBox,
                                                      month2Label, month2ChoiceBox,
                                                      month3Label, month3ChoiceBox,
                                                      month4Label, month4ChoiceBox));
            // Alles anzeigen
            case null, default -> manageChoiceBoxes(List.of(month1Label, month1ChoiceBox,
                                                            month2Label, month2ChoiceBox,
                                                            month3Label, month3ChoiceBox,
                                                            month4Label, month4ChoiceBox),
                                                    List.of());
        }
    }

    private void manageChoiceBoxes(List<Control> elementsToShow, List<Control> elementsToHide) {
        elementsToHide.forEach(el -> el.setVisible(false));
        elementsToShow.forEach(el -> {
            el.setVisible(true);
            if (el instanceof ChoiceBox<?> cb) {
                cb.getSelectionModel().clearSelection();
            }
        });
    }

    @Override
    protected PaymentInformationDTO createEmptyEntity() {
        return new PaymentInformationDTO();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> onRevert());
    }
}
