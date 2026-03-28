package de.timkodiert.mokka.budget;

import java.time.YearMonth;
import java.util.List;

import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;

public interface BudgetService {

    List<Reference<CategoryDTO>> findCategoriesWithActiveBudget();
    @Nullable BudgetState getBudgetState(Reference<CategoryDTO> categoryRef, YearMonth yearMonth);
    @Nullable BudgetInfo getBudgetInfo(Reference<CategoryDTO> categoryRef);
}
