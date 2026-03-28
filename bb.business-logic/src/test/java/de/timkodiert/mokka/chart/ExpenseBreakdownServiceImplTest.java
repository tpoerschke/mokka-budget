package de.timkodiert.mokka.chart;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import de.timkodiert.mokka.TestDataProvider;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;
import de.timkodiert.mokka.domain.repository.CategoriesRepository;
import de.timkodiert.mokka.domain.repository.UniqueExpenseInformationRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseBreakdownServiceImplTest {

    private static final String LABEL_OTHERS = "monthlyOverview.label.others";

    private static final String CATEGORY_LARGEST = "Mobilität";
    private static final String CATEGORY_SECOND_LARGEST = "Wohnen";
    private static final String CATEGORY_WITHOUT_EXPENSES = "Unterhaltung";

    private static final int YEAR = 2025;
    private static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, 11);
    private static final int EXPENSE_VALUE = -100;

    @Mock
    private CategoriesRepository categoriesRepository;
    @Mock
    private UniqueExpenseInformationRepository uniqueExpenseInformationRepository;

    @BeforeEach
    void setUp() {
        Category category1 = new Category();
        category1.setName(CATEGORY_SECOND_LARGEST);

        Category category2 = new Category();
        category2.setName(CATEGORY_LARGEST);

        Category category3 = new Category();
        category3.setName(CATEGORY_WITHOUT_EXPENSES);

        TestDataProvider.createFixedTurnoverWithCategory("Miete", EXPENSE_VALUE, MonthYear.of(YEAR_MONTH), null, category1);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Tanken", EXPENSE_VALUE, category2);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Parken", EXPENSE_VALUE, category2);
        Mockito.when(categoriesRepository.findAll()).thenReturn(List.of(category1, category2, category3));

        // Ohne Kategorie
        UniqueTurnover otherTurnover = TestDataProvider.createUniqueTurnover(YEAR_MONTH.atDay(10), "Fast Food", EXPENSE_VALUE);
        List<UniqueTurnoverInformation> otherTurnoverInfoList = List.of(otherTurnover.getPaymentInformations().getFirst());
        Mockito.when(uniqueExpenseInformationRepository.findAllWithoutFixedTurnoverAndCategory(any())).thenReturn(otherTurnoverInfoList);
    }

    @Test
    @DisplayName("Alle Kategorien mit Ausgaben sollten in der Aufteilung enthalten sein, inklusive 'Sonstige' für Ausgaben ohne Kategorie")
    void getExpenseBreakdown_WithMultipleCategories() {
        ExpenseBreakdownServiceImpl sut = new ExpenseBreakdownServiceImpl(uniqueExpenseInformationRepository, categoriesRepository);
        List<ExpenseBreakdown> result = sut.getExpenseBreakdown(YEAR_MONTH);

        assertEquals(3, result.size());
        assertCategory(result, CATEGORY_LARGEST, Math.abs(EXPENSE_VALUE * 2));
        assertCategory(result, CATEGORY_SECOND_LARGEST, Math.abs(EXPENSE_VALUE));
        assertCategory(result, LABEL_OTHERS, Math.abs(EXPENSE_VALUE));
        assertTrue(result.stream().noneMatch(eb -> eb.category().name().equals(CATEGORY_WITHOUT_EXPENSES)));
    }

    private void assertCategory(List<ExpenseBreakdown> result, String categoryName, int expectedValue) {
        List<ExpenseBreakdown> matching = result.stream().filter(eb -> eb.category().name().equals(categoryName)).toList();
        assertEquals(1, matching.size(), String.format("Es sollte genau eine Kategorie mit Namen '%s' geben", categoryName));
        assertEquals(expectedValue, matching.getFirst().value(), String.format("Der Wert für Kategorie '%s' sollte %d sein", categoryName, expectedValue));
    }

    @Test
    @DisplayName("Die Aufteilung sollte nach Wert sortiert sein (absteigend)")
    void getExpenseBreakdown_SortedByValue() {
        ExpenseBreakdownServiceImpl sut = new ExpenseBreakdownServiceImpl(uniqueExpenseInformationRepository, categoriesRepository);
        List<ExpenseBreakdown> result = sut.getExpenseBreakdown(YEAR_MONTH);

        var largestBreakdown = result.getFirst();
        var secondLargestBreakdown = result.get(1);
        var othersBreakdown = result.getLast();
        assertTrue(largestBreakdown.value() >= secondLargestBreakdown.value());
        assertEquals(LABEL_OTHERS, othersBreakdown.category().name(), "Die 'Sonstige'-Kategorie sollte immer am Ende der Liste stehen");
    }

    @Test
    @DisplayName("Keine 'Sonstige'-Kategorie, wenn es keine Ausgaben ohne Kategorie gibt")
    void getExpenseBreakdown_NoExpensesWithoutCategory() {
        Mockito.when(uniqueExpenseInformationRepository.findAllWithoutFixedTurnoverAndCategory(any())).thenReturn(List.of());
        ExpenseBreakdownServiceImpl sut = new ExpenseBreakdownServiceImpl(uniqueExpenseInformationRepository, categoriesRepository);
        List<ExpenseBreakdown> result = sut.getExpenseBreakdown(YEAR_MONTH);
        assertTrue(result.stream().noneMatch(eb -> eb.category().name().equals(LABEL_OTHERS)));
    }
}



