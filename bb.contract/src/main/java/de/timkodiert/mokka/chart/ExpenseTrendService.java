package de.timkodiert.mokka.chart;

import java.time.YearMonth;
import java.util.List;

public interface ExpenseTrendService {

    List<ExpenseTrend> getExpenseTrendLast12Months(YearMonth selectedMonth);
}

