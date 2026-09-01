package de.timkodiert.mokka.view.monthly_overview;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

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
import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.util.CollectionUtils.IndexValue;
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
                                  LanguageManager languageManager,
                                  @Assisted HalfDonutChart halfDonutChart,
                                  @Assisted MonthFilter monthFilter,
                                  @Assisted ObjectProperty<Reference<CategoryDTO>> categoryProperty) {
        this.expenseBreakdownService = expenseBreakdownService;
        this.halfDonutChart = halfDonutChart;
        this.monthFilter = monthFilter;
        this.categoryProperty = categoryProperty;
        halfDonutChart.setLanguageManager(languageManager);

        halfDonutChart.focusedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (muteListener) {
                return;
            }
            muteListener = true;
            int newIntVal = newValue.intValue();
            if (newIntVal == -1) {
                categoryProperty.setValue(null);
            } else {
                Reference<CategoryDTO> selectedCategory = expenseBreakdownList.get(newIntVal).category();
                categoryProperty.setValue(selectedCategory.id() == -1 ? null : selectedCategory);
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
        loadData();
    }

    public void loadDataAndUpdateFocusedIndex() {
        loadData();
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
        List<Integer> values = expenseBreakdownList.stream().map(ExpenseBreakdown::value).toList();
        List<Integer> percentages = calculatePercentages(values, total);
        var dataList = IntStream.range(0, expenseBreakdownList.size())
                                .mapToObj(i -> {
                                    ExpenseBreakdown breakdown = expenseBreakdownList.get(i);
                                    return new HalfDonutChart.Data(breakdown.category().name(),
                                                                   colorConverter.fromString(breakdown.hexColor()),
                                                                   percentages.get(i));
                                })
                                .toList();
        halfDonutChart.showData(dataList);
    }

    private static List<Integer> calculatePercentages(List<Integer> values, int total) {
        int[] percentages = new int[values.size()];
        int sum = 0;
        List<IndexValue<Double>> remainders = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            double exact = (double) values.get(i) / total * 100;
            percentages[i] = (int) Math.floor(exact);
            sum += percentages[i];
            remainders.add(new IndexValue<>(i, exact - percentages[i]));
        }
        remainders.sort(Comparator.comparing(IndexValue<Double>::value).reversed());
        for (int i = 0; i < 100 - sum; i++) {
            percentages[remainders.get(i).i()]++;
        }
        return IntStream.of(percentages).boxed().toList();
    }
}
