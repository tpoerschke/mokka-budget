package de.timkodiert.mokka.view.monthly_overview;

import dagger.assisted.AssistedFactory;
import javafx.scene.chart.BarChart;

import de.timkodiert.mokka.view.MonthFilter;

@AssistedFactory
public interface ExpenseTrendWidgetFactory {

    ExpenseTrendWidget create(BarChart<String, Double> barChart, MonthFilter monthFilter);
}

