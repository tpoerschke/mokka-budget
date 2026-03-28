package de.timkodiert.mokka.view.fixed_turnover;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Provider;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.converter.BbCurrencyStringConverter;
import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.domain.AccountTurnoverDTO;
import de.timkodiert.mokka.domain.CategoryCrudService;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.FixedTurnoverCrudService;
import de.timkodiert.mokka.domain.FixedTurnoverDTO;
import de.timkodiert.mokka.domain.ImportRuleDTO;
import de.timkodiert.mokka.domain.PaymentInformationDTO;
import de.timkodiert.mokka.domain.PaymentType;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.TurnoverDirection;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.table.cell.DateTableCell;
import de.timkodiert.mokka.table.cell.YearMonthTableCell;
import de.timkodiert.mokka.ui.helper.Bind;
import de.timkodiert.mokka.util.StageBuilder;
import de.timkodiert.mokka.validation.ValidationResult;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.EntityBaseDetailView;

import static de.timkodiert.mokka.view.FxmlResource.FIXED_TURNOVER_INFORMATION_VIEW;

public class FixedTurnoverDetailView extends EntityBaseDetailView<FixedTurnoverDTO> implements Initializable {

    @FXML
    private Pane root;

    @FXML
    private TextField positionTextField;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private ComboBox<TurnoverDirection> directionComboBox;
    @FXML
    private CheckBox payInfoFutureOnlyCheckBox;
    @FXML
    private ComboBox<Reference<CategoryDTO>> categoryComboBox;

    @FXML
    private Button addFixedExpenseInformationButton;
    @FXML
    private Button editFixedExpenseInformationButton;
    @FXML
    private Button deleteFixedExpenseInformationButton;
    @FXML
    private TableView<PaymentInformationDTO> paymentInformationTableView;
    @FXML
    private TableColumn<PaymentInformationDTO, String> expenseInfoValueCol;
    @FXML
    private TableColumn<PaymentInformationDTO, String> expenseInfoTypeCol;
    @FXML
    private TableColumn<PaymentInformationDTO, YearMonth> expenseInfoStartCol;
    @FXML
    private TableColumn<PaymentInformationDTO, YearMonth> expenseInfoEndCol;

    // Importe
    @FXML
    private TableView<ImportRuleDTO> importRuleTable;
    @FXML
    private TableColumn<ImportRuleDTO, Boolean> importRuleActiveCol;
    @FXML
    private TableColumn<ImportRuleDTO, String> importRuleReceiverContainsCol;
    @FXML
    private TableColumn<ImportRuleDTO, String> importRuleReferenceContainsCol;
    @FXML
    private TableView<AccountTurnoverDTO> importsTable;
    @FXML
    private TableColumn<AccountTurnoverDTO, LocalDate> importsDateCol;
    @FXML
    private TableColumn<AccountTurnoverDTO, String> importsReceiverCol;
    @FXML
    private TableColumn<AccountTurnoverDTO, String> importsReferenceCol;
    @FXML
    private TableColumn<AccountTurnoverDTO, String> importsAmountCol;

    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;

    private final FixedTurnoverInformationDetailViewFactory fixedTurnoverInformationDetailViewFactory;
    private final Provider<StageBuilder> stageBuilderProvider;
    private final CategoryCrudService categoryCrudService;
    private final FixedTurnoverCrudService crudService;
    private final LanguageManager languageManager;

    @SuppressWarnings({"java:S1450", "FieldCanBeLocal"}) // Liegt nur hier, damit sie nicht garbage-collected wird
    private SortedList<AccountTurnoverDTO> sortedAccountTurnoverList;

    @Inject
    public FixedTurnoverDetailView(ValidationWrapperFactory<FixedTurnoverDTO> validationWrapperFactory,
                                   FixedTurnoverInformationDetailViewFactory fixedTurnoverInformationDetailViewFactory,
                                   Provider<StageBuilder> stageBuilderProvider,
                                   CategoryCrudService categoryCrudService,
                                   FixedTurnoverCrudService crudService,
                                   LanguageManager languageManager) {
        super(validationWrapperFactory);
        this.fixedTurnoverInformationDetailViewFactory = fixedTurnoverInformationDetailViewFactory;
        this.stageBuilderProvider = stageBuilderProvider;
        this.categoryCrudService = categoryCrudService;
        this.crudService = crudService;
        this.languageManager = languageManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bind bind = new Bind(beanAdapter);

        saveButton.disableProperty().bind(beanAdapter.dirty().not());
        discardButton.disableProperty().bind(beanAdapter.dirty().not());
        addFixedExpenseInformationButton.setGraphic(new FontIcon(BootstrapIcons.PLUS));
        editFixedExpenseInformationButton.setGraphic(new FontIcon(BootstrapIcons.PENCIL));
        deleteFixedExpenseInformationButton.setGraphic(new FontIcon(BootstrapIcons.TRASH));
        editFixedExpenseInformationButton.disableProperty()
                                         .bind(paymentInformationTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteFixedExpenseInformationButton.disableProperty()
                                           .bind(paymentInformationTableView.getSelectionModel().selectedItemProperty().isNull());

        root.disableProperty().bind(beanAdapter.isEmpty());

        // Tabelle der Unterelemente
        StringConverter<PaymentType> paymentTypeConverter = Converters.get(PaymentType.class);
        expenseInfoTypeCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(paymentTypeConverter.toString(cellData.getValue().getType())));
        expenseInfoValueCol.setCellValueFactory(cellData -> {
            BbCurrencyStringConverter converter = new BbCurrencyStringConverter();
            return new ReadOnlyStringWrapper(converter.toString(cellData.getValue().getValue()));
        });
        expenseInfoStartCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStart()));
        expenseInfoEndCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEnd()));
        expenseInfoStartCol.setCellFactory(col -> new YearMonthTableCell<>());
        expenseInfoEndCol.setCellFactory(col -> new YearMonthTableCell<>());

        // Importe
        bind.editableTableColumn(importRuleActiveCol, ImportRuleDTO::isActive, ImportRuleDTO::setActive);
        bind.editableTableColumn(importRuleReceiverContainsCol, ImportRuleDTO::getReceiverContains, ImportRuleDTO::setReceiverContains);
        bind.editableTableColumn(importRuleReferenceContainsCol, ImportRuleDTO::getReferenceContains, ImportRuleDTO::setReferenceContains);
        importsDateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        importsDateCol.setCellFactory(col -> new DateTableCell<>());
        importsReceiverCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReceiver()));
        importsReferenceCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReference()));
        importsAmountCol.setCellValueFactory(cellData -> {
            BbCurrencyStringConverter converter = new BbCurrencyStringConverter();
            return new ReadOnlyStringWrapper(converter.toString(cellData.getValue().getAmount()));
        });
        importsDateCol.setSortType(TableColumn.SortType.DESCENDING);
        importsTable.getSortOrder().add(importsDateCol);

        // Kategorien
        List<Reference<CategoryDTO>> categories = categoryCrudService.readAllAsReference();
        Bind.comboBoxNullable(categoryComboBox, beanAdapter.getProperty(FixedTurnoverDTO::getCategory, FixedTurnoverDTO::setCategory), categories);

        // Bindings
        positionTextField.textProperty().bindBidirectional(beanAdapter.getProperty(FixedTurnoverDTO::getPosition, FixedTurnoverDTO::setPosition));
        noteTextArea.textProperty().bindBidirectional(beanAdapter.getProperty(FixedTurnoverDTO::getNote, FixedTurnoverDTO::setNote));
        Bind.comboBox(directionComboBox,
                      beanAdapter.getProperty(FixedTurnoverDTO::getDirection, FixedTurnoverDTO::setDirection),
                      Arrays.asList(TurnoverDirection.values()),
                      TurnoverDirection.class);
        payInfoFutureOnlyCheckBox.selectedProperty().bindBidirectional(beanAdapter.getProperty(FixedTurnoverDTO::isUsePaymentInfoForFutureOnly,
                                                                                               FixedTurnoverDTO::setUsePaymentInfoForFutureOnly));
        Bindings.bindContentBidirectional(paymentInformationTableView.getItems(),
                                          beanAdapter.getListProperty(FixedTurnoverDTO::getPaymentInformations, FixedTurnoverDTO::setPaymentInformations));
        Bindings.bindContentBidirectional(importRuleTable.getItems(), beanAdapter.getListProperty(FixedTurnoverDTO::getImportRules, FixedTurnoverDTO::setImportRules));
        sortedAccountTurnoverList = new SortedList<>(beanAdapter.getListProperty(FixedTurnoverDTO::getAccountTurnover, FixedTurnoverDTO::setAccountTurnover),
                                                     Comparator.comparing(AccountTurnoverDTO::getDate).reversed());
        Bindings.bindContent(importsTable.getItems(), sortedAccountTurnoverList);

        // Validierung initialisieren
        validationMap.put("position", positionTextField);
        validationMap.put("direction", directionComboBox);
        validationWrapper.register(beanAdapter.getProperty(FixedTurnoverDTO::getPosition, FixedTurnoverDTO::setPosition),
                                   beanAdapter.getProperty(FixedTurnoverDTO::getDirection, FixedTurnoverDTO::setDirection));
        validationWrapper.registerCustomValidation("paymentsOverlapping",
                                                   paymentInformationTableView,
                                                   () -> !beanAdapter.isEmpty().get() && beanAdapter.getBean().hasOverlappingPaymentInformations()
                                                           ? ValidationResult.valid()
                                                           : ValidationResult.error("{fixedTurnover.info.notOverlapping}"),
                                                   beanAdapter.getListProperty(FixedTurnoverDTO::getPaymentInformations, FixedTurnoverDTO::setPaymentInformations));
    }

    @Override
    public boolean save() {
        FixedTurnoverDTO bean = getBean();
        if (bean == null) {
            return false;
        }

        if (!validate()) {
            return false;
        }

        boolean success;
        if (bean.getId() <= 0) {
            success = crudService.create(bean) > 0;
        } else {
            success = crudService.update(bean);
        }

        if (success) {
            beanAdapter.setDirty(false);
            onUpdate.accept(bean);
            return true;
        }
        return false;
    }

    @Override
    protected FixedTurnoverDTO discardChanges() {
        return Optional.ofNullable(crudService.readById(Objects.requireNonNull(getBean()).getId())).orElseGet(this::createEmptyEntity);
    }

    @FXML
    private void deleteExpense(ActionEvent event) {
        FixedTurnoverDTO bean = Objects.requireNonNull(getBean());
        String confirmationText = "Den Umsatz \"%s\" wirklich löschen?".formatted(bean.getPosition());
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, confirmationText, ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.filter(ButtonType.YES::equals).isPresent()) {
            crudService.delete(bean.getId());
            setBean(null);
            onUpdate.accept(null);
        }
    }

    @FXML
    private void addImportRule() {
        importRuleTable.getItems().add(new ImportRuleDTO());
        beanAdapter.setDirty(true);
    }

    @FXML
    private void removeImportRule() {
        ImportRuleDTO selectedImportRule = importRuleTable.getSelectionModel().getSelectedItem();
        if (selectedImportRule != null) {
            importRuleTable.getItems().remove(selectedImportRule);
            beanAdapter.setDirty(true);
        }
    }

    @FXML
    private void newExpenseInformation(ActionEvent event) {
        openTurnoverInformationDetailView(new PaymentInformationDTO());
    }

    @FXML
    private void editExpenseInformation(ActionEvent event) {
        openTurnoverInformationDetailView(paymentInformationTableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void deleteExpenseInformation(ActionEvent event) {
        PaymentInformationDTO payInfoToRemove = paymentInformationTableView.getSelectionModel().getSelectedItem();
        paymentInformationTableView.getItems().remove(payInfoToRemove);
        beanAdapter.setDirty(true);
    }

    private void updateExpenseInformation(@Nullable PaymentInformationDTO oldVal, PaymentInformationDTO newVal) {
        // oldVal nicht null, wenn die Entity in der SubView verworfen wird, da aus der geklonten Backup-Bean wiederhergestellt wird.
        if (oldVal != null) {
            int replaceIndex = paymentInformationTableView.getItems().indexOf(oldVal);
            if (replaceIndex != -1) {
                paymentInformationTableView.getItems().set(replaceIndex, newVal);
            }
        } else {
            int index = paymentInformationTableView.getItems().indexOf(newVal);
            if (index < 0) {
                paymentInformationTableView.getItems().add(newVal);
            }
        }
        paymentInformationTableView.refresh();
        beanAdapter.setDirty(true);
    }

    private void openTurnoverInformationDetailView(PaymentInformationDTO payInfo) {
        try {
            var subDetailView = fixedTurnoverInformationDetailViewFactory.create(this::updateExpenseInformation);
            Stage stage = stageBuilderProvider.get()
                                              .withModality(Modality.APPLICATION_MODAL)
                                              .withOwner(Window.getWindows().getFirst())
                                              .withFXMLResource(FIXED_TURNOVER_INFORMATION_VIEW.toString())
                                              .withView(subDetailView)
                                              .build()
                                              .stage();
            subDetailView.setBean(payInfo);
            subDetailView.setStage(stage);
            stage.show();
        } catch (Exception e) {
            StackTraceAlert.createAndLog(languageManager.get("alert.viewCouldNotBeOpened"), e).showAndWait();
        }
    }

    @Override
    protected FixedTurnoverDTO createEmptyEntity() {
        return new FixedTurnoverDTO();
    }
}
