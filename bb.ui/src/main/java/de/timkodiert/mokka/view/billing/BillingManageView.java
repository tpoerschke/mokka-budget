package de.timkodiert.mokka.view.billing;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.converter.ConverterConstants;
import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.dialog.DialogFactory;
import de.timkodiert.mokka.domain.BillingCrudService;
import de.timkodiert.mokka.domain.BillingDTO;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.table.cell.CurrencyTableCell;
import de.timkodiert.mokka.view.FxmlResource;
import de.timkodiert.mokka.view.mdv_base.BaseListManageView;

public class BillingManageView extends BaseListManageView<BillingDTO> {

    @FXML
    private TableColumn<BillingDTO, String> titleColumn;
    @FXML
    private TableColumn<BillingDTO, String> periodColumn;
    @FXML
    private TableColumn<BillingDTO, Double> totalColumn;

    private final BillingCrudService crudService;

    @Inject
    public BillingManageView(FXMLLoader fxmlLoader, DialogFactory dialogFactory, LanguageManager languageManager, BillingCrudService crudService) {
        super(fxmlLoader, dialogFactory, languageManager);
        this.crudService = crudService;
    }

    @Override
    public void displayEntityById(int id) {
        detailView.setBean(crudService.readById(id));
    }

    @Override
    protected void initControls() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        periodColumn.setCellValueFactory(param -> new SimpleStringProperty(getPeriodString(param.getValue())));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        totalColumn.setCellFactory(col -> new CurrencyTableCell<>());
    }

    private String getPeriodString(BillingDTO billingDTO) {
        if (billingDTO == null || billingDTO.getUniqueTurnovers().isEmpty()) {
            return ConverterConstants.NULL_STRING;
        }
        LocalDate min = billingDTO.getFirstTurnoverDate();
        LocalDate max = billingDTO.getLastTurnoverDate();
        StringConverter<LocalDate> localDateStringConverter = Converters.get(LocalDate.class);
        return String.format("%s - %s", localDateStringConverter.toString(min), localDateStringConverter.toString(max));
    }

    @Override
    protected BillingDTO createEmptyEntity() {
        return new BillingDTO();
    }

    @Override
    protected void reloadTable(@Nullable BillingDTO updatedBean) {
        List<BillingDTO> sortedBillings = crudService.readAll()
                                                     .stream()
                                                     .sorted(Comparator.comparing(
                                                             BillingDTO::getFirstTurnoverDate,
                                                             Comparator.nullsFirst(Comparator.reverseOrder())
                                                     ))
                                                     .toList();
        entityTable.getItems().setAll(sortedBillings);
    }

    @FXML
    private void openNewBilling() {
        entityTable.getSelectionModel().clearSelection();
        lastSelectedRow = null;
        displayNewEntity();
    }

    @Override
    protected String getDetailViewFxmlLocation() {
        return FxmlResource.BILLING_DETAIL_VIEW.getPath();
    }

    @Override
    protected BillingDTO discardChanges(BillingDTO beanToDiscard) {
        return Optional.ofNullable(crudService.readById(beanToDiscard.getId())).orElseGet(this::createEmptyEntity);
    }
}
