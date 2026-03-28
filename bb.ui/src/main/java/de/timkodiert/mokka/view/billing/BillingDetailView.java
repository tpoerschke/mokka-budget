package de.timkodiert.mokka.view.billing;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javax.inject.Provider;

import atlantafx.base.theme.Styles;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import de.timkodiert.mokka.dialog.StackTraceAlert;
import de.timkodiert.mokka.domain.BillingCrudService;
import de.timkodiert.mokka.domain.BillingDTO;
import de.timkodiert.mokka.domain.SimplifiedUniqueTurnoverDTO;
import de.timkodiert.mokka.domain.UniqueTurnoverCrudService;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.representation.RowType;
import de.timkodiert.mokka.table.cell.CurrencyTableCell;
import de.timkodiert.mokka.table.cell.DateTableCell;
import de.timkodiert.mokka.util.StageBuilder;
import de.timkodiert.mokka.validation.ValidationWrapperFactory;
import de.timkodiert.mokka.view.mdv_base.EntityBaseDetailView;

import static de.timkodiert.mokka.view.FxmlResource.UNIQUE_TURNOVER_SELECTOR_MODAL;

public class BillingDetailView extends EntityBaseDetailView<BillingDTO> implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TableView<SimplifiedUniqueTurnoverDTO> turnoverTable;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, String> billerColumn;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, LocalDate> dateColumn;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, Double> valueColumn;

    @FXML
    private TableView<SimplifiedUniqueTurnoverDTO> totalTable;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, String> totalBillerColumn;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, String> totalDateColumn;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, Double> totalValueColumn;

    @FXML
    private Button addTurnoverButton;
    @FXML
    private Button removeTurnoverButton;

    @FXML
    private Button saveButton;
    @FXML
    private Button discardButton;

    private final BillingCrudService crudService;
    private final UniqueTurnoverCrudService uniqueTurnoverCrudService;
    private final LanguageManager languageManager;
    private final Provider<StageBuilder> stageBuilderProvider;

    @Inject
    public BillingDetailView(ValidationWrapperFactory<BillingDTO> validationWrapperFactory,
                             BillingCrudService crudService,
                             UniqueTurnoverCrudService uniqueTurnoverCrudService,
                             LanguageManager languageManager,
                             Provider<StageBuilder> stageBuilderProvider) {
        super(validationWrapperFactory);
        this.crudService = crudService;
        this.uniqueTurnoverCrudService = uniqueTurnoverCrudService;
        this.languageManager = languageManager;
        this.stageBuilderProvider = stageBuilderProvider;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.disableProperty().bind(beanAdapter.isEmpty());
        saveButton.disableProperty().bind(beanAdapter.dirty().not());
        discardButton.disableProperty().bind(beanAdapter.dirty().not());

        removeTurnoverButton.disableProperty().bind(turnoverTable.getSelectionModel().selectedItemProperty().isNull());

        // Tabelle konfigurieren
        initializeTable();

        // Bindings
        titleTextField.textProperty().bindBidirectional(beanAdapter.getProperty(BillingDTO::getTitle, BillingDTO::setTitle));
        descriptionTextArea.textProperty().bindBidirectional(beanAdapter.getProperty(BillingDTO::getDescription, BillingDTO::setDescription));
        Bindings.bindContentBidirectional(turnoverTable.getItems(),
                                          beanAdapter.getListProperty(BillingDTO::getUniqueTurnovers, BillingDTO::setUniqueTurnovers));

        // Validierungen
        validationMap.put("title", titleTextField);
        validationWrapper.register(beanAdapter.getProperty(BillingDTO::getTitle, BillingDTO::setTitle));
    }

    private void initializeTable() {
        billerColumn.setCellValueFactory(new PropertyValueFactory<>("biller"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(col -> new DateTableCell<>());
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        valueColumn.setCellFactory(col -> new CurrencyTableCell<>());
        turnoverTable.getStyleClass().addAll(Styles.BORDERED);
        turnoverTable.getSortOrder().add(dateColumn);

        totalBillerColumn.setCellValueFactory(new PropertyValueFactory<>("biller"));
        totalDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalValueColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        totalValueColumn.setCellFactory(col -> new CurrencyTableCell<>());
    }

    @Override
    protected void beanSet() {
        turnoverTable.sort();
        updateTotalTable();
    }

    @Override
    protected BillingDTO createEmptyEntity() {
        return new BillingDTO();
    }

    @Override
    public boolean save() {
        BillingDTO bean = getBean();
        if (bean == null) {
            return false;
        }
        Predicate<BillingDTO> servicePersistMethod = bean.isNew() ? crudService::create : crudService::update;
        boolean success = validate() && servicePersistMethod.test(bean);
        if (success) {
            beanAdapter.setDirty(false);
            onUpdate.accept(bean);
            return true;
        }
        return false;
    }

    @Override
    protected BillingDTO discardChanges() {
        return Optional.ofNullable(crudService.readById(Objects.requireNonNull(getBean()).getId())).orElseGet(this::createEmptyEntity);
    }

    @FXML
    private void delete(ActionEvent event) {
        BillingDTO billingDTO = this.getBean();
        if (billingDTO != null) {
            crudService.delete(billingDTO.getId());
            beanAdapter.setBean(null);
            onUpdate.accept(null);
        }
    }

    @FXML
    private void addTurnover(ActionEvent event) {
        try {
            UniqueTurnoverSelectorModalView selectorView = new UniqueTurnoverSelectorModalView(uniqueTurnoverCrudService);
            selectorView.setSelectionCallback(this::addSelectedTurnoverToTable);

            Stage modalStage = stageBuilderProvider.get()
                                                   .withFXMLResource(UNIQUE_TURNOVER_SELECTOR_MODAL.toString())
                                                   .withModality(Modality.APPLICATION_MODAL)
                                                   .withOwner(Window.getWindows().getFirst())
                                                   .withTitle(languageManager.get("billingDV.modal.selectTurnover"))
                                                   .minSize(650, 500)
                                                   .withView(selectorView)
                                                   .build()
                                                   .stage();
            selectorView.setStage(modalStage);
            modalStage.showAndWait();
        } catch (IOException e) {
            StackTraceAlert.createAndLog("Modal konnte nicht geöffnet werden", e).showAndWait();
        }
    }

    @FXML
    private void removeTurnover() {
        SimplifiedUniqueTurnoverDTO selectedTurnover = turnoverTable.getSelectionModel().getSelectedItem();
        if (selectedTurnover != null) {
            turnoverTable.getItems().remove(selectedTurnover);
            beanAdapter.setDirty(true);
            turnoverTable.sort();
            updateTotalTable();
        }
    }

    private void addSelectedTurnoverToTable(SimplifiedUniqueTurnoverDTO turnover) {
        boolean alreadyExists = turnoverTable.getItems().stream().anyMatch(t -> Objects.equals(t.getId(), turnover.getId()));
        if (!alreadyExists) {
            turnoverTable.getItems().add(turnover);
            beanAdapter.setDirty(true);
            turnoverTable.sort();
            updateTotalTable();
        }
    }

    private void updateTotalTable() {
        int totalValue = beanAdapter.getBean().getTotalValue();
        totalTable.getItems().clear();
        totalTable.getItems().add(new SimplifiedUniqueTurnoverDTO(-1, languageManager.get("billingDV.label.total"), null, totalValue, RowType.TOTAL_SUM));
    }
}

