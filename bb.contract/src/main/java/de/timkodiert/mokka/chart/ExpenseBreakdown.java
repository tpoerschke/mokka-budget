package de.timkodiert.mokka.chart;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;

public record ExpenseBreakdown(Reference<CategoryDTO> category, int value) {
}
