package de.timkodiert.mokka.budget;

import java.time.YearMonth;
import java.util.List;

import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.repository.CategoriesRepository;

public class BudgetServiceImpl implements BudgetService {

    private final CategoriesRepository categoryRepository;

    @Inject
    public BudgetServiceImpl(CategoriesRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Reference<CategoryDTO>> findCategoriesWithActiveBudget() {
        return categoryRepository.findAll()
                                 .stream()
                                 .filter(Category::hasActiveBudget)
                                 .map(c -> new Reference<>(CategoryDTO.class, c.getId(), c.getName()))
                                 .toList();
    }

    @Override
    public @Nullable BudgetState getBudgetState(Reference<CategoryDTO> categoryRef, YearMonth yearMonth) {
        Category category = categoryRepository.findById(categoryRef.id());
        if (category == null || !category.hasActiveBudget()) {
            return null;
        }
        return new BudgetState(category.getBudgetValue(), Math.abs(category.sumTurnovers(MonthYear.of(yearMonth))));
    }

    @Override
    public @Nullable BudgetInfo getBudgetInfo(Reference<CategoryDTO> categoryRef) {
        Category category = categoryRepository.findById(categoryRef.id());
        if (category == null || !category.hasActiveBudget()) {
            return null;
        }
        return new BudgetInfo(category.getBudgetValue(), category.getBudgetType());
    }
}
