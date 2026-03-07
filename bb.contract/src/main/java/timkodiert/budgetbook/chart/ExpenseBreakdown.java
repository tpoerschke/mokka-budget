package timkodiert.budgetbook.chart;

import timkodiert.budgetbook.domain.CategoryDTO;
import timkodiert.budgetbook.domain.Reference;

public record ExpenseBreakdown(Reference<CategoryDTO> category, int value) {
}
