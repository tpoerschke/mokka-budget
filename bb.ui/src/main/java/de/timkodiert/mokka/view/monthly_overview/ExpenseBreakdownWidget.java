package de.timkodiert.mokka.view.monthly_overview;

import java.util.List;
import java.util.Objects;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import de.timkodiert.mokka.chart.ExpenseBreakdown;
import de.timkodiert.mokka.chart.ExpenseBreakdownService;
import de.timkodiert.mokka.chart.HalfDonutChart;
import de.timkodiert.mokka.converter.Converters;
import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.view.MonthFilter;

public class ExpenseBreakdownWidget {

    private final ExpenseBreakdownService expenseBreakdownService;

    private final HalfDonutChart halfDonutChart;
    private final MonthFilter monthFilter;
    private final ObjectProperty<Reference<CategoryDTO>> categoryProperty;

    private List<ExpenseBreakdown> expenseBreakdownList;
    private boolean muteListener;

    @AssistedInject
    public ExpenseBreakdownWidget(ExpenseBreakdownService expenseBreakdownService,
                                  @Assisted HalfDonutChart halfDonutChart,
                                  @Assisted MonthFilter monthFilter,
                                  @Assisted ObjectProperty<Reference<CategoryDTO>> categoryProperty) {
        this.expenseBreakdownService = expenseBreakdownService;
        this.halfDonutChart = halfDonutChart;
        this.monthFilter = monthFilter;
        this.categoryProperty = categoryProperty;

        halfDonutChart.focusedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (muteListener) {
                return;
            }
            muteListener = true;
            int newIntVal = newValue.intValue();
            if (newIntVal == -1) {
                categoryProperty.setValue(null);
            } else {
                categoryProperty.setValue(expenseBreakdownList.get(newIntVal).category());
            }
            muteListener = false;
        });
        categoryProperty.addListener(((observable, oldValue, newValue) -> {
            if (muteListener) {
                return;
            }
            muteListener = true;
            halfDonutChart.focusedIndexProperty().set(calculateFocusedIndex(newValue));
            muteListener = false;
        }));
        monthFilter.addListener((observable, oldValue, newValue) -> loadData());
        loadData();
    }

    public void updateFocusedIndex() {
        halfDonutChart.focusedIndexProperty().set(calculateFocusedIndex(categoryProperty.getValue()));
    }

    private int calculateFocusedIndex(Reference<CategoryDTO> selectedCategory) {
        return expenseBreakdownList.stream()
                                   .filter(eb -> Objects.equals(eb.category(), selectedCategory))
                                   .findAny()
                                   .map(expenseBreakdownList::indexOf)
                                   .orElse(-1);
    }

    private void loadData() {
        StringConverter<Color> colorConverter = Converters.get(Color.class);
        expenseBreakdownList = expenseBreakdownService.getExpenseBreakdown(monthFilter.getValue());
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
