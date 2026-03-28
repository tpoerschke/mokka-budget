package de.timkodiert.mokka.view.monthly_overview;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import javafx.scene.chart.PieChart;

import de.timkodiert.mokka.chart.ExpenseBreakdownService;
import de.timkodiert.mokka.view.MonthFilter;

public class ExpenseBreakdownWidget {

    private final ExpenseBreakdownService expenseBreakdownService;

    private final PieChart pieChart;
    private final MonthFilter monthFilter;

    @AssistedInject
    public ExpenseBreakdownWidget(ExpenseBreakdownService expenseBreakdownService,
                                  @Assisted PieChart pieChart,
                                  @Assisted MonthFilter monthFilter) {
        this.expenseBreakdownService = expenseBreakdownService;
        this.pieChart = pieChart;
        this.monthFilter = monthFilter;

        monthFilter.addListener((observable, oldValue, newValue) -> loadData());
        loadData();
    }

    private void loadData() {
        var dataList = expenseBreakdownService.getExpenseBreakdown(monthFilter.getValue())
                                              .stream()
                                              .map(b -> new PieChart.Data(b.category().name(), b.value()))
                                              .toList();
        pieChart.getData().setAll(dataList);
    }
}
