package de.timkodiert.mokka.view;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.inject.Inject;

import atlantafx.base.theme.Styles;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import de.timkodiert.mokka.SystemClock;
import de.timkodiert.mokka.annual_overview.AnnualOverviewDTO;
import de.timkodiert.mokka.annual_overview.AnnualOverviewService;
import de.timkodiert.mokka.annual_overview.TableRowData;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.table.cell.CurrencyTableCell;
import de.timkodiert.mokka.table.cell.StylableTableCell;
import de.timkodiert.mokka.table.cell.style.BoldText;
import de.timkodiert.mokka.table.cell.style.CellStyle;
import de.timkodiert.mokka.table.cell.style.DarkerTopBorder;
import de.timkodiert.mokka.table.cell.style.GrayBackground;
import de.timkodiert.mokka.util.CollectionUtils;

import static de.timkodiert.mokka.UiConstants.TABLE_ROW_HEIGHT;

public class AnnualOverviewView implements Initializable, View {

    @FXML
    private Button prevYearBtn;
    @FXML
    private Button nextYearBtn;
    @FXML
    private ComboBox<Integer> selectedYearBox;

    @FXML
    private TableView<TableRowData> mainTable;
    @FXML
    private TableColumn<TableRowData, String> mainTableLabelColumn;

    @FXML
    private TableView<TableRowData> topTable;
    @FXML
    private TableColumn<TableRowData, String> topLabelColumn;

    private final LanguageManager languageManager;
    private final AnnualOverviewService annualOverviewService;

    private YearFilter yearFilter;

    @Inject
    public AnnualOverviewView(LanguageManager languageManager, AnnualOverviewService annualOverviewService) {
        this.languageManager = languageManager;
        this.annualOverviewService = annualOverviewService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        yearFilter = new YearFilter(selectedYearBox, prevYearBtn, nextYearBtn, SystemClock.getYearMonthNow().getYear());
        yearFilter.addListener((observable, oldValue, newValue) -> loadAndDisplayData());
        loadAndDisplayData();

        topLabelColumn.setCellFactory(col -> new StylableTableCell<>(new BoldText(provideCellStyle())));
        topLabelColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(languageManager.get(cellData.getValue().label())));
        mainTableLabelColumn.setCellFactory(col -> new StylableTableCell<>(new BoldText(provideCellStyle())));
        mainTableLabelColumn.setCellValueFactory(cellData -> {
            String label = cellData.getValue().label();
            if (languageManager.resourceBundleContainsKey(label)) {
                label = languageManager.get(label);
            }
            return new ReadOnlyStringWrapper(label);
        });

        CollectionUtils.enumerate(LanguageManager.MONTH_NAMES).forEach(indexValue -> {
            topTable.getColumns().add(createMonthColumn(indexValue));
            mainTable.getColumns().add(createMonthColumn(indexValue));
        });

        topTable.getStyleClass().add(Styles.BORDERED);
        mainTable.getStyleClass().add(Styles.BORDERED);

        topTable.getColumns().add(createCumulativeColumn());
        mainTable.getColumns().add(createCumulativeColumn());
    }

    private void loadAndDisplayData() {
        AnnualOverviewDTO viewData = annualOverviewService.generateOverview(yearFilter.getValue());

        SortedMap<Reference<CategoryDTO>, List<TableRowData>> categoryRowDataMap = new TreeMap<>(Comparator.comparing(Reference::id));
        categoryRowDataMap.putAll(viewData.expensesRowData().stream().collect(Collectors.groupingBy(TableRowData::category)));

        topTable.getItems().clear();
        topTable.getItems().add(viewData.earningsSum());
        topTable.getItems().add(viewData.expensesSum());
        topTable.getItems().add(viewData.totalSum());

        mainTable.getItems().clear();
        categoryRowDataMap.keySet().forEach(key -> {
            mainTable.getItems().add(TableRowData.forCategory(key));
            categoryRowDataMap.get(key).forEach(mainTable.getItems()::add);
        });
        mainTable.getItems().add(viewData.expensesSum());
        mainTable.setMinHeight(TABLE_ROW_HEIGHT * mainTable.getItems().size() + TABLE_ROW_HEIGHT);
    }

    private TableColumn<TableRowData, Number> createMonthColumn(CollectionUtils.IndexValue<String> indexValue) {
        int index = indexValue.i() + 1;
        String month = languageManager.get(indexValue.value());

        TableColumn<TableRowData, Number> tableColumn = new TableColumn<>(month);
        tableColumn.setPrefWidth(120);
        tableColumn.setResizable(false);
        tableColumn.setCellFactory(col -> new CurrencyTableCell<>(provideCellStyle()));
        tableColumn.setCellValueFactory(cellData -> {
            TableRowData rowData = cellData.getValue();
            return new ReadOnlyDoubleWrapper(rowData.monthValueMap().getOrDefault(index, 0));
        });
        return tableColumn;
    }

    private TableColumn<TableRowData, Number> createCumulativeColumn() {
        TableColumn<TableRowData, Number> cumulativeColumn = new TableColumn<>(languageManager.get("annualOverview.label.year"));
        cumulativeColumn.setPrefWidth(120);
        cumulativeColumn.setResizable(false);
        cumulativeColumn.setCellFactory(col -> new CurrencyTableCell<>(provideCellStyle()));
        cumulativeColumn.setCellValueFactory(cellData -> {
            TableRowData rowData = cellData.getValue();
            return new ReadOnlyDoubleWrapper(rowData.monthValueMap().values().stream().mapToInt(Integer::intValue).sum());
        });
        return cumulativeColumn;
    }

    private CellStyle provideCellStyle() {
        return new GrayBackground(new DarkerTopBorder());
    }
}
