package de.timkodiert.mokka.annual_overview;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;

import de.timkodiert.mokka.TestDataProvider;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.repository.FixedExpensesRepository;
import de.timkodiert.mokka.domain.repository.UniqueTurnoverRepository;

@ExtendWith(MockitoExtension.class)
class AnnualOverviewServiceImplTest {

    private static final String LABEL_OTHERS = "annualOverview.label.others";

    private static final int YEAR = 2025;
    private static final YearMonth YEAR_MONTH_1 = YearMonth.of(YEAR, 11);
    private static final YearMonth YEAR_MONTH_2 = YearMonth.of(YEAR, 12);
    private static final int EXPENSE_VALUE = -10;
    private static final int INCOME_VALUE = 10;

    @Mock
    private FixedExpensesRepository fixedTurnoverRepository;
    @Mock
    private UniqueTurnoverRepository uniqueTurnoverRepository;

    @BeforeEach
    void setUp() {
        List<UniqueTurnover> utList = List.of(TestDataProvider.createUniqueTurnover(YEAR_MONTH_1.atDay(1), "UT1", EXPENSE_VALUE),
                                              TestDataProvider.createUniqueTurnover(YEAR_MONTH_2.atDay(1), "UT2", EXPENSE_VALUE),
                                              TestDataProvider.createUniqueTurnover(YEAR_MONTH_2.atDay(1), "UT3", EXPENSE_VALUE),
                                              TestDataProvider.createUniqueTurnover(YEAR_MONTH_2.atDay(1), "UT4", INCOME_VALUE));
        Mockito.when(uniqueTurnoverRepository.findAllWithoutFixedExpense(anyInt())).thenReturn(utList);

        List<FixedTurnover> ftList = List.of(TestDataProvider.createFixedTurnover("FT1", EXPENSE_VALUE, MonthYear.of(YEAR_MONTH_2), null),
                                             TestDataProvider.createFixedTurnover("FT2", INCOME_VALUE, MonthYear.of(YEAR_MONTH_1), null));
        Mockito.when(fixedTurnoverRepository.findAll()).thenReturn(ftList);
    }

    @Test
    void generateOverview() {
        AnnualOverviewServiceImpl sut = new AnnualOverviewServiceImpl(fixedTurnoverRepository, uniqueTurnoverRepository);
        AnnualOverviewDTO result = sut.generateOverview(YEAR);

        assertEquals(0, result.expensesRowData().stream().filter(r -> r.label().equals("FT1")).findAny().orElseThrow().monthValueMap().get(11));
        assertEquals(EXPENSE_VALUE, result.expensesRowData().stream().filter(r -> r.label().equals(LABEL_OTHERS)).findAny().orElseThrow().monthValueMap().get(11));
        assertEquals(EXPENSE_VALUE, result.expensesRowData().stream().filter(r -> r.label().equals("FT1")).findAny().orElseThrow().monthValueMap().get(12));
        assertEquals(EXPENSE_VALUE * 2, result.expensesRowData().stream().filter(r -> r.label().equals(LABEL_OTHERS)).findAny().orElseThrow().monthValueMap().get(12));

        assertEquals(INCOME_VALUE, result.earningsSum().monthValueMap().get(11));
        assertEquals(INCOME_VALUE * 2, result.earningsSum().monthValueMap().get(12));

        assertEquals(EXPENSE_VALUE, result.expensesSum().monthValueMap().get(11));
        assertEquals(EXPENSE_VALUE * 3, result.expensesSum().monthValueMap().get(12));

        assertEquals(0, result.totalSum().monthValueMap().get(11));
        assertEquals(EXPENSE_VALUE, result.totalSum().monthValueMap().get(12));
    }
}