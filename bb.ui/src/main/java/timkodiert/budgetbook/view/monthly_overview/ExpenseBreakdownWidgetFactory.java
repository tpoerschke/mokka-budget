package timkodiert.budgetbook.view.monthly_overview;

import dagger.assisted.AssistedFactory;
import javafx.scene.chart.PieChart;

import timkodiert.budgetbook.view.MonthFilter;

@AssistedFactory
public interface ExpenseBreakdownWidgetFactory {

    ExpenseBreakdownWidget create(PieChart pieChart, MonthFilter monthFilter);
}
