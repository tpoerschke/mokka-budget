package de.timkodiert.mokka.view.monthly_overview;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import de.timkodiert.mokka.chart.ExpenseBreakdown;
import de.timkodiert.mokka.chart.ExpenseBreakdownService;
import de.timkodiert.mokka.chart.HalfDonutChart;
import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.view.MonthFilter;

public class ExpenseBreakdownWidget {

    private final ExpenseBreakdownService expenseBreakdownService;

    private final HalfDonutChart halfDonutChart;
    private final MonthFilter monthFilter;

    @AssistedInject
    public ExpenseBreakdownWidget(ExpenseBreakdownService expenseBreakdownService,
                                  @Assisted HalfDonutChart halfDonutChart,
                                  @Assisted MonthFilter monthFilter) {
        this.expenseBreakdownService = expenseBreakdownService;
        this.halfDonutChart = halfDonutChart;
        this.monthFilter = monthFilter;

        monthFilter.addListener((observable, oldValue, newValue) -> loadData());
        loadData();
    }

    private void loadData() {
        StringConverter<Color> colorConverter = Converters.get(Color.class);
        var expenseBreakdownList = expenseBreakdownService.getExpenseBreakdown(monthFilter.getValue());
        int total = expenseBreakdownList.stream().mapToInt(ExpenseBreakdown::value).sum();
        if (total == 0) {
            halfDonutChart.showData(List.of());
            return;
        }
        var dataList = expenseBreakdownList.stream()
                                           .map(b -> new HalfDonutChart.Data(b.category().name(),
                                                                             colorConverter.fromString(b.hexColor()),
                                                                             Math.round((float) b.value() / total * 100)))
                                           .toList();
        halfDonutChart.showData(dataList);
    }
}
