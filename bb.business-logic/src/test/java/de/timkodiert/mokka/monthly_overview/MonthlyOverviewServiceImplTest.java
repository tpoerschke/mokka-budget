package de.timkodiert.mokka.monthly_overview;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import de.timkodiert.mokka.TestDataProvider;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.repository.FixedExpensesRepository;
import de.timkodiert.mokka.domain.repository.UniqueTurnoverRepository;

@ExtendWith(MockitoExtension.class)
class MonthlyOverviewServiceImplTest {

    private static final YearMonth YEAR_MONTH = YearMonth.of(2025, 11);
    private static final YearMonth NEXT_YEAR_MONTH = YEAR_MONTH.plusMonths(1);
    private static final int EXPENSE_VALUE = -1000; // in Cents
    private static final int INCOME_VALUE = 2000; // in Cents

    @Mock
    private UniqueTurnoverRepository uniqueTurnoverRepository;
    @Mock
    private FixedExpensesRepository fixedTurnoverRepository;

    @BeforeEach
    void setUp() {
        List<UniqueTurnover> utList = List.of(TestDataProvider.createUniqueTurnover(YEAR_MONTH.atDay(1), "UT1", EXPENSE_VALUE),
                                              TestDataProvider.createUniqueTurnover(YEAR_MONTH.atDay(1), "UT2", INCOME_VALUE));
        Mockito.when(uniqueTurnoverRepository.findAllWithoutFixedExpense(any())).thenReturn(utList);

        List<FixedTurnover> ftList = List.of(TestDataProvider.createFixedTurnover("FT1", EXPENSE_VALUE, MonthYear.of(YEAR_MONTH), null),
                                             TestDataProvider.createFixedTurnover("FT2", EXPENSE_VALUE, MonthYear.of(NEXT_YEAR_MONTH), null),
                                             TestDataProvider.createFixedTurnover("FT3", INCOME_VALUE, MonthYear.of(YEAR_MONTH), null));
        Mockito.when(fixedTurnoverRepository.findAll()).thenReturn(ftList);
    }

    @Test
    void generateOverview() {
        MonthlyOverviewServiceImpl sut = new MonthlyOverviewServiceImpl(fixedTurnoverRepository, uniqueTurnoverRepository);
        MonthlyOverviewDTO result = sut.generateOverview(YEAR_MONTH);

        assertEquals(1, result.fixedExpenses().size());
        TableRowData ftTrd = result.fixedExpenses().getFirst();
        assertEquals("FT1", ftTrd.label());
        assertEquals(EXPENSE_VALUE, ftTrd.value());
        assertEquals("", ftTrd.categoriesString());

        assertEquals(1, result.uniqueExpenses().size());
        TableRowData utTrd = result.uniqueExpenses().getFirst();
        assertEquals("UT1", utTrd.label());
        assertEquals(EXPENSE_VALUE, utTrd.value());
        assertEquals("", utTrd.categoriesString());

        assertEquals(EXPENSE_VALUE * 2, result.totalSumExpenses());
        assertEquals(INCOME_VALUE * 2, result.incomeSum());
    }
}