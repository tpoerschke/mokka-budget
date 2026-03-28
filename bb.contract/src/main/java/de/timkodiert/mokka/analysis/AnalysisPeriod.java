package de.timkodiert.mokka.analysis;

import java.time.Month;
import java.time.YearMonth;
import java.util.List;

import de.timkodiert.mokka.SystemClock;
import de.timkodiert.mokka.util.IntervalUtils;

public enum AnalysisPeriod {
    LAST_12_MONTH, THIS_YEAR;

    public List<YearMonth> getMonths() {
        YearMonth now = SystemClock.getYearMonthNow();
        if (this == AnalysisPeriod.LAST_12_MONTH) {
            return IntervalUtils.yearMonthRange(now.minusMonths(12), now);
        }
        return IntervalUtils.yearMonthRange(YearMonth.of(now.getYear(), Month.JANUARY.getValue()),
                                            YearMonth.of(now.getYear(), Month.DECEMBER.getValue()));
    }
}
