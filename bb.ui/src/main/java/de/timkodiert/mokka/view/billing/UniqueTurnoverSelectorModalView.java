package de.timkodiert.mokka.view.billing;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import atlantafx.base.theme.Styles;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Setter;

import de.timkodiert.mokka.domain.SimplifiedUniqueTurnoverDTO;
import de.timkodiert.mokka.domain.UniqueTurnoverCrudService;
import de.timkodiert.mokka.table.cell.CurrencyTableCell;
import de.timkodiert.mokka.table.cell.DateTableCell;
import de.timkodiert.mokka.view.View;

public class UniqueTurnoverSelectorModalView implements View, Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private TextField filterBillerTextField;
    @FXML
    private DatePicker filterDatePicker;
    @FXML
    private Button filterButton;

    @FXML
    private TableView<SimplifiedUniqueTurnoverDTO> turnoverTable;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, String> billerColumn;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, LocalDate> dateColumn;
    @FXML
    private TableColumn<SimplifiedUniqueTurnoverDTO, Double> valueColumn;

    @FXML
    private Button selectButton;
    @FXML
    private Button cancelButton;

    private final UniqueTurnoverCrudService crudService;
    @Setter
    private Consumer<SimplifiedUniqueTurnoverDTO> selectionCallback;
    @Setter
    private Stage stage;

    private List<SimplifiedUniqueTurnoverDTO> allTurnovers;

    @Inject
    public UniqueTurnoverSelectorModalView(UniqueTurnoverCrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Tabelle konfigurieren
        billerColumn.setCellValueFactory(new PropertyValueFactory<>("biller"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(col -> new DateTableCell<>());
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        valueColumn.setCellFactory(col -> new CurrencyTableCell<>());
        turnoverTable.getStyleClass().addAll(Styles.BORDERED);

        loadAllTurnovers();

        selectButton.disableProperty().bind(turnoverTable.getSelectionModel().selectedItemProperty().isNull());
    }

    private void loadAllTurnovers() {
        allTurnovers = crudService.readSortedByDateDesc(100);
        turnoverTable.getItems().setAll(allTurnovers);
    }

    @FXML
    private void applyFilter(ActionEvent event) {
        List<SimplifiedUniqueTurnoverDTO> filteredTurnovers = allTurnovers.stream().filter(this::matchesFilter).toList();
        turnoverTable.getItems().setAll(filteredTurnovers);
    }

    private boolean matchesFilter(SimplifiedUniqueTurnoverDTO turnover) {
        String billerFilter = filterBillerTextField.getText();
        LocalDate dateFilter = filterDatePicker.getValue();

        if (billerFilter != null && !billerFilter.trim().isEmpty() && !turnover.getBiller().toLowerCase().contains(billerFilter.toLowerCase())) {
            return false;
        }
        return dateFilter == null || (turnover.getDate() != null && turnover.getDate().equals(dateFilter));
    }

    @FXML
    private void selectTurnover(ActionEvent event) {
        SimplifiedUniqueTurnoverDTO selected = turnoverTable.getSelectionModel().getSelectedItem();
        if (selected != null && selectionCallback != null) {
            selectionCallback.accept(selected);
            if (stage != null) {
                stage.close();
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        if (stage != null) {
            stage.close();
        }
    }
}


