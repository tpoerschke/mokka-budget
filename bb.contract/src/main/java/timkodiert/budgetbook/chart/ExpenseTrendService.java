package timkodiert.budgetbook.chart;

import java.time.YearMonth;
import java.util.List;

public interface ExpenseTrendService {

    List<ExpenseTrend> getExpenseTrendLast12Months(YearMonth selectedMonth);
}

