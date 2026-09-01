package de.timkodiert.mokka.chart;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.timkodiert.mokka.TestDataProvider;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;
import de.timkodiert.mokka.domain.repository.CategoriesRepository;
import de.timkodiert.mokka.domain.repository.UniqueExpenseInformationRepository;

import static de.timkodiert.mokka.TestDataProvider.createCategory;

@ExtendWith(MockitoExtension.class)
class ExpenseBreakdownServiceImplTest {

    private static final String LABEL_OTHERS = "monthlyOverview.label.others";

    private static final String CATEGORY_LARGEST = "Mobilität";
    private static final String CATEGORY_SECOND_LARGEST = "Wohnen";
    private static final String CATEGORY_THIRD_LARGEST = "Wocheneinkauf";
    private static final String CATEGORY_FOURTH_LARGEST = "Unterhaltung";
    private static final String CATEGORY_FIFTH_LARGEST = "Hobby";
    private static final String CATEGORY_SIXTH_LARGEST = "Shopping";
    private static final String CATEGORY_SEVENTH_LARGEST = "Versicherungen";
    private static final String CATEGORY_WITHOUT_EXPENSES = "Ohne Ausgaben";

    private static final int YEAR = 2025;
    private static final YearMonth YEAR_MONTH = YearMonth.of(YEAR, 11);
    private static final int EXPENSE_VALUE = -100;

    @Mock
    private CategoriesRepository categoriesRepository;
    @Mock
    private UniqueExpenseInformationRepository uniqueExpenseInformationRepository;

    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {
        category1 = createCategory(CATEGORY_SECOND_LARGEST);
        category2 = createCategory(CATEGORY_LARGEST);
        category3 = createCategory(CATEGORY_THIRD_LARGEST);
        Category category4 = createCategory(CATEGORY_FOURTH_LARGEST);
        Category category5 = createCategory(CATEGORY_FIFTH_LARGEST);
        Category category6 = createCategory(CATEGORY_SIXTH_LARGEST);
        Category category7 = createCategory(CATEGORY_SEVENTH_LARGEST);
        Category category8 = createCategory(CATEGORY_WITHOUT_EXPENSES);

        TestDataProvider.createFixedTurnoverWithCategory("Miete", EXPENSE_VALUE, MonthYear.of(YEAR_MONTH), null, category1);
        TestDataProvider.createFixedTurnoverWithCategory("Strom", EXPENSE_VALUE, MonthYear.of(YEAR_MONTH), null, category1);
        TestDataProvider.createFixedTurnoverWithCategory("Internet", EXPENSE_VALUE, MonthYear.of(YEAR_MONTH), null, category1);
        TestDataProvider.createFixedTurnoverWithCategory("Versicherung", EXPENSE_VALUE, MonthYear.of(YEAR_MONTH), null, category1);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Tanken1", EXPENSE_VALUE, category2);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(16), "Tanken2", EXPENSE_VALUE, category2);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(16), "Tanken3", EXPENSE_VALUE, category2);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Parken1", EXPENSE_VALUE, category2);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(16), "Parken2", EXPENSE_VALUE, category2);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Aldi", EXPENSE_VALUE, category3);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(16), "Lidl", EXPENSE_VALUE, category3);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(17), "Rewe", EXPENSE_VALUE, category3);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Kino1", EXPENSE_VALUE, category4);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(16), "Kino2", EXPENSE_VALUE, category4);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Zeichnen1", EXPENSE_VALUE, category5);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(16), "Zeichnen2", EXPENSE_VALUE, category5);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Ruhr Park", EXPENSE_VALUE, category6);
        TestDataProvider.createUniqueTurnoverWithCategory(YEAR_MONTH.atDay(15), "Haftpflicht", EXPENSE_VALUE, category7);
        when(categoriesRepository.findAll()).thenReturn(List.of(category1, category2, category3, category4, category5, category6, category7, category8));

        // Ohne Kategorie
        UniqueTurnover otherTurnover = TestDataProvider.createUniqueTurnover(YEAR_MONTH.atDay(10), "Fast Food", EXPENSE_VALUE);
        List<UniqueTurnoverInformation> otherTurnoverInfoList = List.of(otherTurnover.getPaymentInformations().getFirst());
        when(uniqueExpenseInformationRepository.findAllWithoutFixedTurnoverAndCategory(any())).thenReturn(otherTurnoverInfoList);
    }

    @Test
    @DisplayName("Die 5 \"teuersten\" Kategorien sollten in der Auflistung enthalten sein + eine 'Sonstige'-Kategorie für alle anderen Ausgaben")
    void getExpenseBreakdown_WithMultipleCategories() {
        ExpenseBreakdownServiceImpl sut = new ExpenseBreakdownServiceImpl(uniqueExpenseInformationRepository, categoriesRepository);
        List<ExpenseBreakdown> result = sut.getExpenseBreakdown(YEAR_MONTH);

        assertEquals(6, result.size());
        assertCategory(result, CATEGORY_LARGEST, Math.abs(EXPENSE_VALUE * 5));
        assertCategory(result, CATEGORY_SECOND_LARGEST, Math.abs(EXPENSE_VALUE * 4));
        assertCategory(result, CATEGORY_THIRD_LARGEST, Math.abs(EXPENSE_VALUE * 3));
        assertCategory(result, CATEGORY_FOURTH_LARGEST, Math.abs(EXPENSE_VALUE * 2));
        assertCategory(result, CATEGORY_FIFTH_LARGEST, Math.abs(EXPENSE_VALUE * 2));
        assertCategory(result, LABEL_OTHERS, Math.abs(EXPENSE_VALUE * 3));
        assertTrue(result.stream().noneMatch(eb -> List.of(CATEGORY_SIXTH_LARGEST, CATEGORY_SEVENTH_LARGEST, CATEGORY_WITHOUT_EXPENSES).contains(eb.category().name())));
    }

    @Test
    @DisplayName("Nur 3 Kategorien, keine Ausgaben ohne Kategorie")
    void getExpenseBreakdown_ThreeCategoriesNoOtherExpenses() {
        when(categoriesRepository.findAll()).thenReturn(List.of(category1, category2, category3));
        when(uniqueExpenseInformationRepository.findAllWithoutFixedTurnoverAndCategory(any())).thenReturn(List.of());

        ExpenseBreakdownServiceImpl sut = new ExpenseBreakdownServiceImpl(uniqueExpenseInformationRepository, categoriesRepository);
        List<ExpenseBreakdown> result = sut.getExpenseBreakdown(YEAR_MONTH);

        assertEquals(3, result.size());
        assertCategory(result, CATEGORY_LARGEST, Math.abs(EXPENSE_VALUE * 5));
        assertCategory(result, CATEGORY_SECOND_LARGEST, Math.abs(EXPENSE_VALUE * 4));
        assertCategory(result, CATEGORY_THIRD_LARGEST, Math.abs(EXPENSE_VALUE * 3));
    }

    @Test
    @DisplayName("Nur 3 Kategorien, 1 Ausgabe ohne Kategorie")
    void getExpenseBreakdown_ThreeCategoriesOneOtherExpenses() {
        when(categoriesRepository.findAll()).thenReturn(List.of(category1, category2, category3));

        ExpenseBreakdownServiceImpl sut = new ExpenseBreakdownServiceImpl(uniqueExpenseInformationRepository, categoriesRepository);
        List<ExpenseBreakdown> result = sut.getExpenseBreakdown(YEAR_MONTH);

        assertEquals(4, result.size());
        assertCategory(result, CATEGORY_LARGEST, Math.abs(EXPENSE_VALUE * 5));
        assertCategory(result, CATEGORY_SECOND_LARGEST, Math.abs(EXPENSE_VALUE * 4));
        assertCategory(result, CATEGORY_THIRD_LARGEST, Math.abs(EXPENSE_VALUE * 3));
        assertCategory(result, LABEL_OTHERS, Math.abs(EXPENSE_VALUE));
    }

    private void assertCategory(List<ExpenseBreakdown> result, String categoryName, int expectedValue) {
        List<ExpenseBreakdown> matching = result.stream().filter(eb -> eb.category().name().equals(categoryName)).toList();
        assertEquals(1, matching.size(), String.format("Es sollte genau eine Kategorie mit Namen '%s' geben", categoryName));
        assertEquals(expectedValue, matching.getFirst().value(), String.format("Der Wert für Kategorie '%s' sollte %d sein", categoryName, expectedValue));
    }

    @Test
    @DisplayName("Die Sortierung sollte nach Wert sortiert sein (absteigend)")
    void getExpenseBreakdown_SortedByValue() {
        ExpenseBreakdownServiceImpl sut = new ExpenseBreakdownServiceImpl(uniqueExpenseInformationRepository, categoriesRepository);
        List<ExpenseBreakdown> result = sut.getExpenseBreakdown(YEAR_MONTH);

        var largestBreakdown = result.getFirst();
        var secondLargestBreakdown = result.get(1);
        var thirdLargestBreakdown = result.get(2);
        var fourthLargestBreakdown = result.get(3);
        var fifthLargestBreakdown = result.get(4);
        var othersBreakdown = result.getLast();
        assertTrue(largestBreakdown.value() >= secondLargestBreakdown.value());
        assertTrue(secondLargestBreakdown.value() >= thirdLargestBreakdown.value());
        assertTrue(thirdLargestBreakdown.value() >= fourthLargestBreakdown.value());
        assertTrue(fourthLargestBreakdown.value() >= fifthLargestBreakdown.value());
        assertEquals(LABEL_OTHERS, othersBreakdown.category().name(), "Die 'Sonstige'-Kategorie sollte immer am Ende der Liste stehen");
    }
}



