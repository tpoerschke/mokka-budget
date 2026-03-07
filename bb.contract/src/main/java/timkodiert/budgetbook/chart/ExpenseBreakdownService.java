package timkodiert.budgetbook.chart;

import java.time.YearMonth;
import java.util.List;

public interface ExpenseBreakdownService {

    List<ExpenseBreakdown> getExpenseBreakdown(YearMonth yearMonth);
}
