package de.timkodiert.mokka.view.monthly_overview;

import dagger.assisted.AssistedFactory;
import javafx.scene.chart.PieChart;

import de.timkodiert.mokka.view.MonthFilter;

@AssistedFactory
public interface ExpenseBreakdownWidgetFactory {

    ExpenseBreakdownWidget create(PieChart pieChart, MonthFilter monthFilter);
}
