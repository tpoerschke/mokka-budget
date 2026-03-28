package de.timkodiert.mokka.view;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Provider;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import de.timkodiert.mokka.budget.BudgetService;
import de.timkodiert.mokka.exception.TechnicalException;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.monthly_overview.MonthlyOverviewDTO;
import de.timkodiert.mokka.monthly_overview.MonthlyOverviewService;
import de.timkodiert.mokka.monthly_overview.TableRowData;
import de.timkodiert.mokka.representation.RowType;
import de.timkodiert.mokka.table.cell.CurrencyTableCell;
import de.timkodiert.mokka.table.cell.DateTableCell;
import de.timkodiert.mokka.table.cell.GroupTableCell;
import de.timkodiert.mokka.table.row.BoldTableRow;
import de.timkodiert.mokka.table.row.ShortcutTableRow;
import de.timkodiert.mokka.view.monthly_overview.ExpenseBreakdownWidgetFactory;
import de.timkodiert.mokka.view.monthly_overview.ExpenseTrendWidgetFactory;
import de.timkodiert.mokka.view.monthly_overview.IconTableCell;
import de.timkodiert.mokka.view.monthly_overview.MonthlyOverviewCurrencyTableCell;
import de.timkodiert.mokka.view.widget.BudgetWidget;

public class MonthlyOverviewView implements Initializable, View {

    // FILTER
    @FXML
    private Button nextMonthBtn;
    @FXML
    private Button prevMonthBtn;
    @FXML
    private ComboBox<String> selectedMonthBox;
    @FXML
    private ComboBox<Integer> selectedYearBox;

    @SuppressWarnings("java:S1450") // Damit der Filter nicht garbage-collected wird
    private MonthFilter monthFilter;

    // TABELLEN
    @FXML
    private TableView<TableRowData> dataTable;
    @FXML
    private TableView<TableRowData> sumTable;
    @FXML
    private TableColumn<TableRowData, String> positionCol;
    @FXML
    private TableColumn<TableRowData, String> categoriesCol;
    @FXML
    private TableColumn<TableRowData, String> sumTableCol0;
    @FXML
    private TableColumn<TableRowData, String> sumTableCol1;
    @FXML
    private TableColumn<TableRowData, Number> valueCol;
    @FXML
    private TableColumn<TableRowData, Number> sumTableCol2;
    @FXML
    private TableColumn<TableRowData, LocalDate> dateCol;
    @FXML
    private TableColumn<TableRowData, TableRowData> buttonCol;
    @FXML
    private TableColumn<TableRowData, TableRowData> iconCol;

    // CHARTS
    @FXML
    private PieChart expenseBreakdownChart;
    @FXML
    private BarChart<String, Double> expenseTrendChart;

    // BUDGETS
    @FXML
    private VBox budgetBox;

    private final ObservableList<TableRowData> tableData = FXCollections.observableArrayList();

    private final Provider<FXMLLoader> fxmlLoader;
    private final LanguageManager languageManager;
    private final MonthlyOverviewService monthlyOverviewService;
    private final BudgetService budgetService;
    private final Provider<ShortcutTableRow> shortcutTableRowProvider;
    private final MonthFilterFactory monthFilterFactory;
    private final ExpenseBreakdownWidgetFactory expenseBreakdownWidgetFactory;
    private final ExpenseTrendWidgetFactory expenseTrendWidgetFactory;

    @Inject
    public MonthlyOverviewView(Provider<FXMLLoader> fxmlLoader,
                               LanguageManager languageManager,
                               MonthlyOverviewService monthlyOverviewService,
                               BudgetService budgetService,
                               Provider<ShortcutTableRow> shortcutTableRowProvider,
                               MonthFilterFactory monthFilterFactory,
                               ExpenseBreakdownWidgetFactory expenseBreakdownWidgetFactory,
                               ExpenseTrendWidgetFactory expenseTrendWidgetFactory) {
        this.fxmlLoader = fxmlLoader;
        this.languageManager = languageManager;
        this.monthlyOverviewService = monthlyOverviewService;
        this.budgetService = budgetService;
        this.shortcutTableRowProvider = shortcutTableRowProvider;
        this.monthFilterFactory = monthFilterFactory;
        this.expenseBreakdownWidgetFactory = expenseBreakdownWidgetFactory;
        this.expenseTrendWidgetFactory = expenseTrendWidgetFactory;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthFilter = monthFilterFactory.create(selectedMonthBox, selectedYearBox, nextMonthBtn, prevMonthBtn);
        //
        // INIT TABELLEN
        //
        sumTableCol0.prefWidthProperty().bind(buttonCol.widthProperty());
        sumTableCol1.prefWidthProperty().bind(positionCol.widthProperty());
        sumTableCol2.prefWidthProperty().bind(valueCol.widthProperty());

        sumTableCol1.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().label()));
        sumTableCol2.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().value()));
        sumTableCol2.setCellFactory(col -> new CurrencyTableCell<>());

        sumTable.setRowFactory(tableView -> new BoldTableRow<>(RowType.TOTAL_SUM));

        loadAndDisplayViewData(YearMonth.now());
        FilteredList<TableRowData> filteredData = new FilteredList<>(tableData);

        SimpleBooleanProperty isUniqueCollapsedProperty = new SimpleBooleanProperty(false);
        SimpleBooleanProperty isFixedCollapsedProperty = new SimpleBooleanProperty(true);
        Map<RowType, BooleanProperty> dataGroupProperties = new EnumMap<>(RowType.class);
        dataGroupProperties.put(RowType.FIXED_EXPENSE_GROUP, isFixedCollapsedProperty);
        dataGroupProperties.put(RowType.UNIQUE_EXPENSE_GROUP, isUniqueCollapsedProperty);
        Runnable predicator = () -> {
            List<RowType> toShow = new ArrayList<>(RowType.getGroupTypes());
            if (!isUniqueCollapsedProperty.get()) {
                toShow.add(RowType.UNIQUE_EXPENSE);
            }
            if (!isFixedCollapsedProperty.get()) {
                toShow.add(RowType.FIXED_EXPENSE);
            }
            filteredData.setPredicate(d -> toShow.contains(d.getRowType()));
        };

        buttonCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue()));
        buttonCol.setCellFactory(col -> new GroupTableCell<>(dataGroupProperties));
        isUniqueCollapsedProperty.addListener((observable, oldValue, newValue) -> predicator.run());
        isFixedCollapsedProperty.addListener((observable, oldValue, newValue) -> predicator.run());
        predicator.run();

        positionCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().label()));
        valueCol.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().value()));
        valueCol.setCellFactory(col -> new MonthlyOverviewCurrencyTableCell(dataGroupProperties));
        dateCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().date()));
        dateCol.setCellFactory(col -> new DateTableCell<>());
        categoriesCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().categoriesString()));
        iconCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue()));
        iconCol.setCellFactory(col -> new IconTableCell(languageManager));

        dataTable.setRowFactory(tv -> shortcutTableRowProvider.get());
        dataTable.getColumns().forEach(col -> col.setReorderable(false));
        dataTable.setItems(filteredData);

        //
        // INIT FILTER
        //
        monthFilter.addListener((observable, oldValue, newValue) -> loadAndDisplayViewData(newValue));

        //
        // INIT CHARTS
        //
        expenseBreakdownWidgetFactory.create(expenseBreakdownChart, monthFilter);
        expenseTrendWidgetFactory.create(expenseTrendChart, monthFilter);

        //
        // INIT BUDGETS
        //
        var categoriesWithBudget = budgetService.findCategoriesWithActiveBudget();
        categoriesWithBudget.forEach(cat -> {
            FXMLLoader loader = fxmlLoader.get();
            loader.setLocation(getClass().getResource(FxmlResource.BUDGET_WIDGET.getPath()));
            try {
                loader.load();
                BudgetWidget budgetWidget = loader.getController();
                budgetWidget.getCategoryProperty().set(cat);
                budgetWidget.getSelectedYearMonthProperty().bind(monthFilter);
                budgetBox.getChildren().add(budgetWidget.getRoot());
            } catch (Exception e) {
                throw TechnicalException.forFxmlNotFound(e);
            }
        });
    }

    private void loadAndDisplayViewData(YearMonth yearMonth) {
        MonthlyOverviewDTO data = monthlyOverviewService.generateOverview(yearMonth);
        initDataGroups(data);
        initFooterTable(data);
    }

    private void initFooterTable(MonthlyOverviewDTO data) {
        int totalSumExpenses = data.totalSumExpenses();
        sumTable.getItems().clear();
        sumTable.getItems().addAll(TableRowData.forSum(languageManager.get("monthlyOverview.label.sumExpenses"), totalSumExpenses),
                                   TableRowData.forSum(languageManager.get("monthlyOverview.label.sumEarnings"), data.incomeSum()),
                                   TableRowData.forTotalSum(languageManager.get("monthlyOverview.label.sum"), data.incomeSum() + totalSumExpenses));
    }

    private void initDataGroups(MonthlyOverviewDTO data) {
        tableData.clear();
        initFixedExpenseGroup(data.fixedExpenses());
        initUniqueExpenseGroup(data.uniqueExpenses());
    }

    private void initUniqueExpenseGroup(List<TableRowData> tableRowDataList) {
        initDataGroup(tableRowDataList, languageManager.get("monthlyOverview.label.uniqueExpenses"), RowType.UNIQUE_EXPENSE_GROUP);
    }

    private void initFixedExpenseGroup(List<TableRowData> expenses) {
        initDataGroup(expenses, languageManager.get("monthlyOverview.label.fixedExpenses"), RowType.FIXED_EXPENSE_GROUP);
    }

    private void initDataGroup(List<TableRowData> tableRowDataList, String groupName, RowType groupRowType) {
        tableData.add(new TableRowData(-1, groupRowType, groupName, null, tableRowDataList.stream().mapToInt(TableRowData::value).sum(), List.of(), false));
        tableData.addAll(tableRowDataList);
    }
}
