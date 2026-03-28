package de.timkodiert.mokka.domain.model;

import java.time.LocalDate;
import java.time.YearMonth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.timkodiert.mokka.domain.TurnoverDirection;

import static de.timkodiert.mokka.domain.model.TestDataProvider.createFixedTurnover;

class FixedTurnoverTest {

    @Test
    @DisplayName("Monatliche Ausgabe: Konfigurierte PaymentInformation")
    void monthlyFixedExpense() {
        FixedTurnover turnover = createFixedTurnover(inCent(9.99), MonthYear.of(1, 2023), null);

        assertEquals(0, turnover.getValueFor(MonthYear.of(12, 2022)));
        assertEquals(-999, turnover.getValueFor(MonthYear.of(1, 2023)));
        assertEquals(0, turnover.getValueForYear(2022));
        assertEquals(-999 * 12, turnover.getValueForYear(2023));
    }

    @Test
    @DisplayName("Monatliche Ausgabe: Betrag der Importe verwenden")
    void monthlyFixedExpenseImportedAmounts() {
        FixedTurnover turnover = createFixedTurnover(950, MonthYear.of(1, 2023), null);
        // Import für Februar vorliegend
        turnover.getUniqueTurnovers().add(TestDataProvider.createUniqueTurnoverWithAccountTurnover(LocalDate.of(2023, 2, 1), "Test 1", -2000));
        // Zwei Importe für März vorliegend
        turnover.getUniqueTurnovers().add(TestDataProvider.createUniqueTurnoverWithAccountTurnover(LocalDate.of(2023, 3, 1), "Test 2", -500));
        turnover.getUniqueTurnovers().add(TestDataProvider.createUniqueTurnoverWithAccountTurnover(LocalDate.of(2023, 3, 2), "Test 3", -500));

        assertEquals(0, turnover.getValueFor(MonthYear.of(2, 2022)));
        assertEquals(inCent(-20.00), turnover.getValueFor(MonthYear.of(2, 2023)));
        assertEquals(2 * -inCent(5), turnover.getValueFor(MonthYear.of(3, 2023)));
        assertEquals(0, turnover.getValueForYear(2022));
        assertEquals(-950 * 10 + -2000 + 2 * -500, turnover.getValueForYear(2023));
    }

    @Test
    @DisplayName("Monatliche Ausgabe: Betrag der Importe verwenden, aber konfigurierter Betrag an der konkreten Ausgabe gewinnt")
    void monthlyFixedTurnoverImportsButUniqueTurnoverHasDifferentValue() {
        FixedTurnover turnover = createFixedTurnover(950, MonthYear.of(1, 2023), null);
        // Import für Februar vorliegend
        turnover.getUniqueTurnovers().add(TestDataProvider.createUniqueTurnoverWithAccountTurnover(LocalDate.of(2023, 2, 1), "Test 1", -2000));
        UniqueTurnoverInformation info = turnover.getUniqueTurnovers().getFirst().getPaymentInformations().getFirst();
        info.setValue(100);
        info.setDirection(TurnoverDirection.IN);

        assertEquals(100, turnover.getValueFor(MonthYear.of(2, 2023)));
    }

    @Test
    @DisplayName("Monatliche Ausgabe: Konfigurierte PaymentInformation, werden nicht für die Vergangenheit angewendet")
    void monthlyFixedExpenseNotForPast() {
        // Arrange
        YearMonth current = YearMonth.now();
        YearMonth start = current.minusMonths(1);
        YearMonth end = current.plusMonths(1);
        FixedTurnover turnover = createFixedTurnover(999, MonthYear.of(start), MonthYear.of(end));
        turnover.setUsePaymentInfoForFutureOnly(true);

        // Act & Assert
        assertEquals(0, turnover.getValueFor(MonthYear.of(start)));
        assertEquals(-999, turnover.getValueFor(MonthYear.of(current)));
        assertEquals(-999, turnover.getValueFor(MonthYear.of(end)));
    }

    private int inCent(double value) {
        return (int) (value * 100);
    }
}
