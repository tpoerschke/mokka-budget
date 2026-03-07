package timkodiert.budgetbook.view.monthly_overview;

import dagger.assisted.AssistedFactory;
import javafx.scene.chart.BarChart;

import timkodiert.budgetbook.view.MonthFilter;

@AssistedFactory
public interface ExpenseTrendWidgetFactory {

    ExpenseTrendWidget create(BarChart<String, Double> barChart, MonthFilter monthFilter);
}

