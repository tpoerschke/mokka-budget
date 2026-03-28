package de.timkodiert.mokka.view.unique_turnover;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Provider;

import atlantafx.base.controls.Message;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jspecify.annotations.Nullable;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.converter.BbCurrencyStringConverter;
import de.timkodiert.mokka.converter.ReferenceStringConverter;
import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.domain.AccountTurnoverDTO;
import de.timkodiert.mokka.domain.FixedTurnoverCrudService;
import de.timkodiert.mokka.domain.FixedTurnoverDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.UniqueTurnoverCrudService;
import de.timkodiert.mokka.domain.UniqueTurnoverDTO;
import de.timkodiert.mokka.domain.UniqueTurnoverInformationDTO;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.ui.control.AutoCompleteTextField;
import de.timkodiert.mokka.ui.helper.Bind;
import de.timkodiert.mokka.util.StageBuilder;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.EntityBaseDetailView;

import static de.timkodiert.mokka.util.ObjectUtils.nvl;
import static de.timkodiert.mokka.view.FxmlResource.IMAGE_VIEW;
import static de.timkodiert.mokka.view.FxmlResource.UNIQUE_TURNOVER_INFORMATION_VIEW;

public class UniqueTurnoverDetailView extends EntityBaseDetailView<UniqueTurnoverDTO> implements Initializable {

    @FXML
    private Pane root;
    @FXML
    private AutoCompleteTextField billerTextField;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField receiptTextField;
    @FXML
    private ImageView receiptImageView;

    @FXML
    private Button addUniqueExpenseInformationButton;
    @FXML
    private Button editUniqueExpenseInformationButton;
    @FXML
    private Button deleteUniqueExpenseInformationButton;

    @FXML
    private TableView<UniqueTurnoverInformationDTO> expenseInfoTable;
    @FXML
    private TableColumn<UniqueTurnoverInformationDTO, String> expenseInfoPositionCol;
    @FXML
    private TableColumn<UniqueTurnoverInformationDTO, String> expenseInfoValueCol;
    @FXML
    private TableColumn<UniqueTurnoverInformationDTO, String> expenseInfoCategoriesCol;

    @FXML
    private ComboBox<Reference<FixedTurnoverDTO>> fixedTurnoverComboBox;

    @FXML
    private TextField importReceiverTextField;
    @FXML
    private TextField importReferenceTextField;
    @FXML
    private TextField importPostingTextTextField;
    @FXML
    private TextField importAmountTextField;
    @FXML
    private Message amountWarningMessage;

    @FXML
    private ColumnConstraints rightColumn;

    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;

    private final BbCurrencyStringConverter bbCurrencyStringConverter;
    private final UniqueTurnoverCrudService crudService;
    private final FixedTurnoverCrudService fixedTurnoverCrudService;
    private final LanguageManager languageManager;
    private final Provider<StageBuilder> stageBuilderProvider;
    private final UniqueTurnoverInformationDetailViewFactory uniqueTurnoverInformationDetailViewFactory;

    @Inject
    public UniqueTurnoverDetailView(ValidationWrapperFactory<UniqueTurnoverDTO> validationWrapperFactory,
                                    BbCurrencyStringConverter bbCurrencyStringConverter,
                                    UniqueTurnoverCrudService crudService,
                                    FixedTurnoverCrudService fixedTurnoverCrudService,
                                    LanguageManager languageManager,
                                    Provider<StageBuilder> stageBuilderProvider,
                                    UniqueTurnoverInformationDetailViewFactory uniqueTurnoverInformationDetailViewFactory) {
        super(validationWrapperFactory);
        this.bbCurrencyStringConverter = bbCurrencyStringConverter;
        this.crudService = crudService;
        this.fixedTurnoverCrudService = fixedTurnoverCrudService;
        this.languageManager = languageManager;
        this.stageBuilderProvider = stageBuilderProvider;
        this.uniqueTurnoverInformationDetailViewFactory = uniqueTurnoverInformationDetailViewFactory;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveButton.disableProperty().bind(beanAdapter.dirty().not());
        discardButton.disableProperty().bind(beanAdapter.dirty().not());
        addUniqueExpenseInformationButton.setGraphic(new FontIcon(BootstrapIcons.PLUS));
        editUniqueExpenseInformationButton.setGraphic(new FontIcon(BootstrapIcons.PENCIL));
        deleteUniqueExpenseInformationButton.setGraphic(new FontIcon(BootstrapIcons.TRASH));
        editUniqueExpenseInformationButton.disableProperty()
                .bind(expenseInfoTable.getSelectionModel().selectedItemProperty().isNull());
        deleteUniqueExpenseInformationButton.disableProperty()
                .bind(expenseInfoTable.getSelectionModel().selectedItemProperty().isNull());

        root.disableProperty().bind(beanAdapter.isEmpty());

        receiptTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank()) {
                receiptImageView.setImage(new Image(new File(newValue).toURI().toString()));
            } else {
                receiptImageView.setImage(null);
            }
        });
        receiptImageView.setOnMouseClicked(event -> {
            String path = receiptTextField.getText();
            if (path == null || path.isBlank()) {
                return;
            }
            try {
                stageBuilderProvider.get()
                                    .withFXMLResource(IMAGE_VIEW.toString())
                                    .withModality(Modality.APPLICATION_MODAL)
                                    .withTitle("Beleg / Kassenbon")
                                    .withView(new ImageModalView(languageManager, path))
                                    .build()
                                    .stage()
                                    .showAndWait();
            } catch (IOException e) {
                StackTraceAlert.createAndLog("ImageModal konnte nicht geöffnet werden", e).showAndWait();
            }
        });

        expenseInfoPositionCol
                .setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getLabel()));
        expenseInfoValueCol.setCellValueFactory(cellData -> {
            BbCurrencyStringConverter converter = new BbCurrencyStringConverter();
            return new ReadOnlyStringWrapper(converter.toString(cellData.getValue().getValueSigned()));
        });
        expenseInfoCategoriesCol.setCellValueFactory(cellData ->
                                                             new ReadOnlyStringWrapper(nvl(cellData.getValue().getCategory(), Reference::name)));

        billerTextField.getAvailableEntries().addAll(crudService.getUniqueTurnoverLabels());

        fixedTurnoverComboBox.setConverter(new ReferenceStringConverter<>());
        fixedTurnoverComboBox.getItems().add(null);
        fixedTurnoverComboBox.getItems().addAll(fixedTurnoverCrudService.findAllAsReference());

        // Bindings
        billerTextField.textProperty().bindBidirectional(beanAdapter.getProperty(UniqueTurnoverDTO::getBiller, UniqueTurnoverDTO::setBiller));
        datePicker.valueProperty().bindBidirectional(beanAdapter.getProperty(UniqueTurnoverDTO::getDate, UniqueTurnoverDTO::setDate));
        noteTextArea.textProperty().bindBidirectional(beanAdapter.getProperty(UniqueTurnoverDTO::getNote, UniqueTurnoverDTO::setNote));
        receiptTextField.textProperty().bindBidirectional(beanAdapter.getProperty(UniqueTurnoverDTO::getReceiptImagePath, UniqueTurnoverDTO::setReceiptImagePath));

        Bindings.bindContentBidirectional(expenseInfoTable.getItems(), beanAdapter.getListProperty(UniqueTurnoverDTO::getPaymentInformations,
                                                                                                   UniqueTurnoverDTO::setPaymentInformations));

        Bind.comboBox(fixedTurnoverComboBox, beanAdapter.getProperty(UniqueTurnoverDTO::getFixedTurnover, UniqueTurnoverDTO::setFixedTurnover));

        // Validierungen
        validationMap.put("biller", billerTextField);
        validationMap.put("date", datePicker);
        validationWrapper.register(beanAdapter.getProperty(UniqueTurnoverDTO::getBiller, UniqueTurnoverDTO::setBiller),
                                   beanAdapter.getProperty(UniqueTurnoverDTO::getDate, UniqueTurnoverDTO::setDate));
    }

    @Override
    protected void beanSet() {
        AccountTurnoverDTO accountTurnover = nvl(beanAdapter.getBean(), UniqueTurnoverDTO::getAccountTurnover);
        importReceiverTextField.setText(nvl(accountTurnover, AccountTurnoverDTO::getReceiver));
        importReferenceTextField.setText(nvl(accountTurnover, AccountTurnoverDTO::getReference));
        importPostingTextTextField.setText(nvl(accountTurnover, AccountTurnoverDTO::getPostingText));
        importAmountTextField.setText(bbCurrencyStringConverter.toString(nvl(accountTurnover, AccountTurnoverDTO::getAmount)));
        updateImportAmountWarning();
    }

    private void updateImportAmountWarning() {
        if (beanAdapter.isEmpty().get() || beanAdapter.getBean().getAccountTurnover() == null) {
            importAmountTextField.setStyle("");
            amountWarningMessage.setVisible(false);
            return;
        }
        double turnoverValue = beanAdapter.getBean().getTotalValue();
        double importedValue = beanAdapter.getBean().getAccountTurnover().getAmount();
        if (turnoverValue == importedValue) {
            importAmountTextField.setStyle("");
            amountWarningMessage.setVisible(false);
        } else {
            importAmountTextField.setStyle("-fx-border-color: -color-warning-2;");
            amountWarningMessage.setVisible(true);
        }
    }

    @Override
    public boolean save() {
        UniqueTurnoverDTO bean = getBean();
        if (bean == null) {
            return false;
        }
        Predicate<UniqueTurnoverDTO> crudMethod = bean.getId() <= 0 ? crudService::create : crudService::update;
        boolean success = validate() && crudMethod.test(getBean());
        if (success) {
            beanAdapter.setDirty(false);
            onUpdate.accept(bean);
            return true;
        }
        return false;
    }

    @Override
    protected UniqueTurnoverDTO discardChanges() {
        return Optional.ofNullable(crudService.readById(Objects.requireNonNull(getBean()).getId())).orElseGet(this::createEmptyEntity);
    }

    @FXML
    private void delete(ActionEvent event) {
        UniqueTurnoverDTO bean = Objects.requireNonNull(getBean());
        String confirmationText = "Den Umsatz \"%s\" wirklich löschen?".formatted(bean.getBiller());
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, confirmationText, ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.filter(ButtonType.YES::equals).isPresent()) {
            crudService.delete(bean.getId());
            setBean(null);
            onUpdate.accept(null);
        }
    }

    @FXML
    private void newUniqueExpenseInformation(ActionEvent event) {
        openUniqueExpenseInformationDetailView(new UniqueTurnoverInformationDTO());
    }

    @FXML
    private void editUniqueExpenseInformation(ActionEvent event) {
        openUniqueExpenseInformationDetailView(expenseInfoTable.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void deleteUniqueExpenseInformation(ActionEvent event) {
        UniqueTurnoverInformationDTO expInfo = expenseInfoTable.getSelectionModel().getSelectedItem();
        expenseInfoTable.getItems().remove(expInfo);
        updateImportAmountWarning();
        beanAdapter.setDirty(true);
    }

    private void updateTurnoverInformation(@Nullable UniqueTurnoverInformationDTO oldVal, UniqueTurnoverInformationDTO newVal) {
        // oldVal nicht null, wenn die Entity in der SubView verworfen wird, da aus der geklonten Backup-Bean wiederhergestellt wird.
        if (oldVal != null) {
            int replaceIndex = expenseInfoTable.getItems().indexOf(oldVal);
            if (replaceIndex != -1) {
                expenseInfoTable.getItems().set(replaceIndex, newVal);
            }
        } else {
            int index = expenseInfoTable.getItems().indexOf(newVal);
            if (index < 0) {
                expenseInfoTable.getItems().add(newVal);
            }
        }
        expenseInfoTable.refresh();
        updateImportAmountWarning();
        beanAdapter.setDirty(true);
    }

    private void openUniqueExpenseInformationDetailView(UniqueTurnoverInformationDTO uniqueTurnover) {
        try {
            var subEntityDetailView = uniqueTurnoverInformationDetailViewFactory.create(this::updateTurnoverInformation);
            Stage stage = stageBuilderProvider.get()
                                              .withModality(Modality.APPLICATION_MODAL)
                                              .withOwner(Window.getWindows().getFirst())
                                              .withFXMLResource(UNIQUE_TURNOVER_INFORMATION_VIEW.toString())
                                              .withView(subEntityDetailView)
                                              .build()
                                              .stage();
            subEntityDetailView.setBean(uniqueTurnover);
            subEntityDetailView.setStage(stage);
            stage.show();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR, languageManager.get("alert.viewCouldNotBeOpened"));
            alert.showAndWait();
            StackTraceAlert.createAndLog(languageManager.get("alert.viewCouldNotBeOpened"), e).showAndWait();
        }
    }

    @Override
    protected UniqueTurnoverDTO createEmptyEntity() {
        return new UniqueTurnoverDTO();
    }
}
