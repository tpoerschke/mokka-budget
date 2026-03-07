package timkodiert.budgetbook.chart;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.IntStream;

import jakarta.inject.Inject;

import timkodiert.budgetbook.domain.model.MonthYear;
import timkodiert.budgetbook.domain.model.UniqueTurnover;
import timkodiert.budgetbook.domain.repository.FixedExpensesRepository;
import timkodiert.budgetbook.domain.repository.UniqueExpensesRepository;

public class ExpenseTrendServiceImpl implements ExpenseTrendService {

    private final FixedExpensesRepository fixedExpensesRepository;
    private final UniqueExpensesRepository uniqueExpensesRepository;

    @Inject
    public ExpenseTrendServiceImpl(FixedExpensesRepository fixedExpensesRepository, UniqueExpensesRepository uniqueExpensesRepository) {
        this.fixedExpensesRepository = fixedExpensesRepository;
        this.uniqueExpensesRepository = uniqueExpensesRepository;
    }

    @Override
    public List<ExpenseTrend> getExpenseTrendLast12Months(YearMonth selectedMonth) {
        return IntStream.range(0, 12)
                        .mapToObj(selectedMonth::minusMonths)
                        .sorted()
                        .map(yearMonth -> new ExpenseTrend(yearMonth, Math.abs(calculateTotalExpensesForMonth(yearMonth))))
                        .toList();
    }

    private int calculateTotalExpensesForMonth(YearMonth yearMonth) {
        MonthYear monthYear = MonthYear.of(yearMonth);

        int fixedExpensesSum = fixedExpensesRepository.findAll()
                                                      .stream()
                                                      .mapToInt(ft -> ft.getValueFor(monthYear))
                                                      .filter(val -> val < 0)
                                                      .sum();

        int uniqueExpensesSum = uniqueExpensesRepository.findAllWithoutFixedExpense(monthYear)
                                                        .stream()
                                                        .mapToInt(UniqueTurnover::getTotalValue)
                                                        .filter(val -> val < 0)
                                                        .sum();

        return fixedExpensesSum + uniqueExpensesSum;
    }
}



