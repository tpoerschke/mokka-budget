package de.timkodiert.mokka.view.monthly_overview;

import dagger.assisted.AssistedFactory;

import de.timkodiert.mokka.chart.HalfDonutChart;
import de.timkodiert.mokka.view.MonthFilter;

@AssistedFactory
public interface ExpenseBreakdownWidgetFactory {

    ExpenseBreakdownWidget create(HalfDonutChart halfDonutChart, MonthFilter monthFilter);
}
