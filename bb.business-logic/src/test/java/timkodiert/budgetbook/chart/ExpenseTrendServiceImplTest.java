package timkodiert.budgetbook.chart;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import timkodiert.budgetbook.TestDataProvider;
import timkodiert.budgetbook.domain.model.FixedTurnover;
import timkodiert.budgetbook.domain.model.MonthYear;
import timkodiert.budgetbook.domain.model.UniqueTurnover;
import timkodiert.budgetbook.domain.repository.FixedExpensesRepository;
import timkodiert.budgetbook.domain.repository.UniqueExpensesRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseTrendServiceImplTest {

    private static final int YEAR = 2025;
    private static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, 12);
    private static final int FIXED_EXPENSE_VALUE = -50;
    private static final int UNIQUE_EXPENSE_VALUE = -30;

    @Mock
    private FixedExpensesRepository fixedExpensesRepository;
    @Mock
    private UniqueExpensesRepository uniqueExpensesRepository;

    @BeforeEach
    void setUp() {
        // Setup fixed expenses
        FixedTurnover fixedTurnover = TestDataProvider.createFixedTurnover("FT1", FIXED_EXPENSE_VALUE, MonthYear.of(YEAR_MONTH.minusMonths(6)), null);
        Mockito.when(fixedExpensesRepository.findAll()).thenReturn(List.of(fixedTurnover));

        // Setup unique expenses for each month
        UniqueTurnover uniqueTurnover1 = TestDataProvider.createUniqueTurnover(YEAR_MONTH.atDay(15), "UT1", UNIQUE_EXPENSE_VALUE);
        UniqueTurnover uniqueTurnover2 = TestDataProvider.createUniqueTurnover(YEAR_MONTH.minusMonths(1).atDay(10), "UT2", UNIQUE_EXPENSE_VALUE);
        Mockito.when(uniqueExpensesRepository.findAllWithoutFixedExpense(any())).thenReturn(List.of(uniqueTurnover1, uniqueTurnover2));
    }

    @Test
    void getExpenseTrendLast12Months_ReturnsCorrectNumberOfMonths() {
        ExpenseTrendServiceImpl sut = new ExpenseTrendServiceImpl(fixedExpensesRepository, uniqueExpensesRepository);
        List<ExpenseTrend> result = sut.getExpenseTrendLast12Months(YEAR_MONTH);
        assertEquals(12, result.size());
    }

    @Test
    void getExpenseTrendLast12Months_ReturnedInChronologicalOrder() {
        ExpenseTrendServiceImpl sut = new ExpenseTrendServiceImpl(fixedExpensesRepository, uniqueExpensesRepository);
        List<ExpenseTrend> result = sut.getExpenseTrendLast12Months(YEAR_MONTH);

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).month().isBefore(result.get(i + 1).month()), "Monate sollten in chronologischer Reihenfolge sein");
        }
    }

    @Test
    void getExpenseTrendLast12Months_IncludesSelectedMonth() {
        ExpenseTrendServiceImpl sut = new ExpenseTrendServiceImpl(fixedExpensesRepository, uniqueExpensesRepository);
        List<ExpenseTrend> result = sut.getExpenseTrendLast12Months(YEAR_MONTH);

        // The last month in the list should be the selected month
        assertEquals(YEAR_MONTH, result.getLast().month());
    }

    @Test
    void getExpenseTrendLast12Months_Includes12MonthsBackward() {
        ExpenseTrendServiceImpl sut = new ExpenseTrendServiceImpl(fixedExpensesRepository, uniqueExpensesRepository);
        List<ExpenseTrend> result = sut.getExpenseTrendLast12Months(YEAR_MONTH);

        // The first month should be 12 months before the selected month
        assertEquals(YEAR_MONTH.minusMonths(11), result.getFirst().month());
    }

    @Test
    void getExpenseTrendLast12Months_AllValuesArePositive() {
        ExpenseTrendServiceImpl sut = new ExpenseTrendServiceImpl(fixedExpensesRepository, uniqueExpensesRepository);
        List<ExpenseTrend> result = sut.getExpenseTrendLast12Months(YEAR_MONTH);

        // All expense values should be positive (Math.abs is applied)
        assertTrue(result.stream().allMatch(trend -> trend.value() >= 0));
    }

    @Test
    void getExpenseTrendLast12Months_ContainsExpenseData() {
        ExpenseTrendServiceImpl sut = new ExpenseTrendServiceImpl(fixedExpensesRepository, uniqueExpensesRepository);
        List<ExpenseTrend> result = sut.getExpenseTrendLast12Months(YEAR_MONTH);

        ExpenseTrend currentMonthTrend = result.stream()
                                               .filter(trend -> trend.month().equals(YEAR_MONTH))
                                               .findAny()
                                               .orElseThrow();
        int expectedValue = Math.abs(FIXED_EXPENSE_VALUE + UNIQUE_EXPENSE_VALUE * 2);
        assertEquals(expectedValue, currentMonthTrend.value());
    }

    @Test
    void getExpenseTrendLast12Months_DifferentSelectedMonth() {
        YearMonth differentMonth = YearMonth.of(2024, 6);

        ExpenseTrendServiceImpl sut = new ExpenseTrendServiceImpl(fixedExpensesRepository, uniqueExpensesRepository);
        List<ExpenseTrend> result = sut.getExpenseTrendLast12Months(differentMonth);

        // Should return 12 months
        assertEquals(12, result.size());

        // Last month should be the selected month
        assertEquals(differentMonth, result.getLast().month());

        // First month should be 12 months before
        assertEquals(differentMonth.minusMonths(11), result.getFirst().month());
    }
}



