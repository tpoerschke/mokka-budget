package de.timkodiert.mokka.view.analysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.net.URL;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import jakarta.inject.Inject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.knowm.xchart.AnnotationLine;
import org.knowm.xchart.AnnotationTextPanel;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import de.timkodiert.mokka.SystemClock;
import de.timkodiert.mokka.analysis.AnalysisPeriod;
import de.timkodiert.mokka.analysis.AnalysisService;
import de.timkodiert.mokka.analysis.CategorySeriesGenerator;
import de.timkodiert.mokka.analysis.TableRowData;
import de.timkodiert.mokka.budget.BudgetInfo;
import de.timkodiert.mokka.budget.BudgetService;
import de.timkodiert.mokka.budget.BudgetType;
import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.converter.ReferenceStringConverter;
import de.timkodiert.mokka.domain.CategoryCrudService;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.table.cell.CurrencyTableCell;
import de.timkodiert.mokka.util.MoneyEssentials;
import de.timkodiert.mokka.view.View;

public class AnalysisView implements View, Initializable {

    private final LanguageManager languageManager;
    private final AnalysisService analysisService;
    private final CategoryCrudService categoryCrudService;
    private final CategorySeriesGenerator categorySeriesGenerator;
    private final BudgetService budgetService;

    @FXML
    private BorderPane root;
    @FXML
    private StackPane chartContainer;
    @FXML
    private ComboBox<AnalysisPeriod> periodComboBox;
    @FXML
    private ComboBox<Reference<CategoryDTO>> categoryComboBox;

    @FXML
    private TableView<TableRowData> turnoverTable;
    @FXML
    private TableColumn<TableRowData, String> turnoverPosition;
    @FXML
    private TableColumn<TableRowData, Number> turnoverValue;

    private CategoryChart chart;

    @Inject
    public AnalysisView(LanguageManager languageManager,
                        AnalysisService analysisService,
                        CategoryCrudService categoryCrudService,
                        CategorySeriesGenerator categorySeriesGenerator,
                        BudgetService budgetService) {
        this.languageManager = languageManager;
        this.analysisService = analysisService;
        this.categoryCrudService = categoryCrudService;
        this.categorySeriesGenerator = categorySeriesGenerator;
        this.budgetService = budgetService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        periodComboBox.setConverter(Converters.get(AnalysisPeriod.class));
        periodComboBox.getItems().addAll(AnalysisPeriod.values());
        categoryComboBox.setConverter(new ReferenceStringConverter<>());
        categoryComboBox.getItems().addAll(categoryCrudService.readAllAsReference());
        periodComboBox.valueProperty().addListener((observable, oldVal, newVal) -> updateChart());
        categoryComboBox.valueProperty().addListener((observable, oldVal, newVal) -> updateChart());

        periodComboBox.getSelectionModel().select(AnalysisPeriod.LAST_12_MONTH);

        turnoverPosition.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().position()));
        turnoverValue.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().value()));
        turnoverValue.setCellFactory(col -> new CurrencyTableCell<>());
    }

    private void updateChart() {
        AnalysisPeriod selectedPeriod = periodComboBox.getSelectionModel().getSelectedItem();
        Reference<CategoryDTO> selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();

        if (selectedPeriod == null || selectedCategory == null) {
            return;
        }

        chart = new CategoryChartBuilder().width(800)
                                          .height(600)
                                          .title(String.format(languageManager.get("analysisView.chart.title"), selectedCategory.name()))
                                          .xAxisTitle(languageManager.get("analysisView.chart.xAxis.month"))
                                          .yAxisTitle(languageManager.get("analysisView.chart.yAxis.expenses"))
                                          .theme(Styler.ChartTheme.GGPlot2)
                                          .build();
        styleChart();

        List<YearMonth> yearMonths = selectedPeriod.getMonths();
        List<String> monthNameList = yearMonths.stream().map(yearMonth -> languageManager.get(LanguageManager.MONTH_NAMES.get(yearMonth.getMonthValue() - 1))).toList();
        CategorySeries s1 = chart.addSeries("Categories", monthNameList, categorySeriesGenerator.generateCategorySeries(selectedPeriod, selectedCategory));
        s1.setFillColor(Color.decode("#27476E"));

        CategorySeries s2 = chart.addSeries("Cumulative",
                                            monthNameList,
                                            categorySeriesGenerator.generateCumulativeCategorySeries(selectedPeriod, selectedCategory));
        s2.setChartCategorySeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Line);
        s2.setLineColor(Color.decode("#16C172"));
        s2.setMarkerColor(Color.decode("#16C172"));

        chart.setYAxisGroupTitle(1, languageManager.get("analysisView.chart.yAxis.cumulativeExpenses"));
        s2.setYAxisGroup(1);
        chart.getStyler().setYAxisGroupPosition(1, Styler.YAxisPosition.Right);

        addBudgetSeries();
        addAnnotations();

        XChartPanel<CategoryChart> panel = new XChartPanel<>(chart);
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(panel);

        swingNode.setOnMouseClicked(this::fillTurnoverTable);

        chartContainer.getChildren().setAll(swingNode);

        turnoverTable.getItems().clear();
    }

    /**
     * Annotationslinien können nicht an der zweiten (rechten) Y-Achse ausgerichtet werden, da dies je nach
     * BudgetType jedoch nötig ist, wird die Budget-Linie als Series dargestellt.
     */
    private void addBudgetSeries() {
        AnalysisPeriod selectedPeriod = periodComboBox.getSelectionModel().getSelectedItem();
        Reference<CategoryDTO> selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        BudgetInfo budgetInfo = budgetService.getBudgetInfo(selectedCategory);

        if (budgetInfo == null) {
            return;
        }

        BudgetType budgetType = budgetInfo.budgetType();
        double budgetValue = MoneyEssentials.asBigDecimal(budgetInfo.budgetValue()).divide(MoneyEssentials.FACTOR_100, MoneyEssentials.ROUNDING_MODE).doubleValue();

        List<YearMonth> yearMonths = selectedPeriod.getMonths();
        List<String> monthNameList = yearMonths.stream().map(yearMonth -> languageManager.get(LanguageManager.MONTH_NAMES.get(yearMonth.getMonthValue() - 1))).toList();

        CategorySeries budgetSeries = chart.addSeries("Budget",
                                                      monthNameList,
                                                      Collections.nCopies(yearMonths.size(), budgetValue));

        if (budgetType == BudgetType.MONTHLY) {
            budgetSeries.setYAxisGroup(0);
        } else {
            budgetSeries.setYAxisGroup(1);
        }

        budgetSeries.setChartCategorySeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Line);
        budgetSeries.setLineStyle(SeriesLines.DASH_DASH);
        budgetSeries.setMarker(SeriesMarkers.NONE);
        budgetSeries.setLineColor(Color.decode("#FF3900"));
        budgetSeries.setLineWidth(30.0f);
    }

    private void addAnnotations() {
        if (periodComboBox.getSelectionModel().getSelectedItem() == AnalysisPeriod.THIS_YEAR) {
            return;
        }
        YearMonth yearMonth = SystemClock.getYearMonthNow();
        int xVal = 12 - yearMonth.getMonthValue() + 1;
        AnnotationLine yearLine = new AnnotationLine(xVal, true, false);
        String monthStr = String.format("%d →", yearMonth.getYear());
        AnnotationTextPanel annotationText = new AnnotationTextPanel(monthStr, xVal, 0, false);
        chart.addAnnotation(yearLine);
        chart.addAnnotation(annotationText);
    }

    private void styleChart() {
        chart.getStyler().setOverlapped(true);
        chart.getStyler().setLabelsVisible(true);
        chart.getStyler().setToolTipsEnabled(true);
        chart.getStyler().setToolTipType(Styler.ToolTipType.yLabels);
        chart.getStyler().setToolTipHighlightColor(Color.decode("#91B7C7"));
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotGridVerticalLinesVisible(false);

        chart.getStyler().setAnnotationLineStroke(new BasicStroke(2));
        chart.getStyler().setAnnotationTextPanelBackgroundColor(Color.WHITE);
    }

    private void fillTurnoverTable(MouseEvent event) {
        AnalysisPeriod analysisPeriod = periodComboBox.getSelectionModel().getSelectedItem();
        int chartX = (int) chart.getChartXFromCoordinate((int) event.getScreenX());
        YearMonth yearMonth;
        if (analysisPeriod == AnalysisPeriod.LAST_12_MONTH) {
            yearMonth = SystemClock.getYearMonthNow().minusMonths(12);
        } else {
            yearMonth = SystemClock.getYearNow().atMonth(Month.JANUARY);
        }
        yearMonth = yearMonth.plusMonths(chartX);

        Reference<CategoryDTO> category = categoryComboBox.getValue();
        List<TableRowData> rowData = analysisService.getTurnoverList(category, yearMonth);
        turnoverTable.getItems().clear();
        turnoverTable.getItems().addAll(rowData);
    }
}
