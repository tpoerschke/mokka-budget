package de.timkodiert.mokka.util;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

public class IntervalUtils {

    private static final YearMonth MAX_YEAR_MONTH = YearMonth.of(9999, 12);

    private IntervalUtils() {
        // Statische Klasse
    }

    public static boolean overlap(YearMonth start1, @Nullable YearMonth end1, YearMonth start2, @Nullable YearMonth end2) {
        YearMonth effectiveEnd1 = (end1 == null) ? MAX_YEAR_MONTH : end1;
        YearMonth effectiveEnd2 = (end2 == null) ? MAX_YEAR_MONTH : end2;
        return !start1.isAfter(effectiveEnd2) && !start2.isAfter(effectiveEnd1);
    }

    public static List<YearMonth> yearMonthRange(YearMonth start, YearMonth end) {
        if (end.isBefore(start)) {
            return List.of();
        }
        ArrayList<YearMonth> monthYearList = new ArrayList<>();
        YearMonth current = start;
        while (!current.isAfter(end)) {
            monthYearList.add(current);
            current = current.plusMonths(1);
        }
        return monthYearList;
    }
}
