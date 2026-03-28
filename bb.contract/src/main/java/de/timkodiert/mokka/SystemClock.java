package de.timkodiert.mokka;

import java.time.Year;
import java.time.YearMonth;

public class SystemClock {

    private SystemClock() {
        // Statische Klasse
    }

    public static YearMonth getYearMonthNow() {
        return YearMonth.now(); // für Testdaten: YearMonth.of(2025, 8)
    }

    public static Year getYearNow() {
        return Year.now(); // für Testdaten: Year.of(2025)
    }
}
