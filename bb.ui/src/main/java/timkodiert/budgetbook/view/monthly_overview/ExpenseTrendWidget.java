package timkodiert.budgetbook.view.monthly_overview;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import timkodiert.budgetbook.chart.ExpenseTrendService;
import timkodiert.budgetbook.view.MonthFilter;

public class ExpenseTrendWidget {

    private final ExpenseTrendService expenseTrendService;
    private final BarChart<String, Double> barChart;
    private final MonthFilter monthFilter;

    @AssistedInject
    public ExpenseTrendWidget(ExpenseTrendService expenseTrendService,
                              @Assisted BarChart<String, Double> barChart,
                              @Assisted MonthFilter monthFilter) {
        this.expenseTrendService = expenseTrendService;
        this.barChart = barChart;
        this.monthFilter = monthFilter;

        monthFilter.addListener((observable, oldValue, newValue) -> loadData());
        loadData();
    }

    private void loadData() {
        var trends = expenseTrendService.getExpenseTrendLast12Months(monthFilter.getValue());

        XYChart.Series<String, Double> series = new XYChart.Series<>();
        trends.forEach(trend -> {
            String monthLabel = String.format("%02d.%d", trend.month().getMonthValue(), trend.month().getYear());
            var xyData = new XYChart.Data<>(monthLabel, trend.value() / 100.0); // Rundungsfehler hier ok, da ja nur eine grobe Übersicht
            xyData.nodeProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal instanceof StackPane bar) {
                    bar.setBackground(new Background(new BackgroundFill(Color.web("#0969DAFF"), null, null)));
                }
            });
            series.getData().add(xyData);
        });

        barChart.getData().clear();
        barChart.getData().add(series);
    }
}

