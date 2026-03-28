package de.timkodiert.mokka.analysis;

import java.time.YearMonth;
import java.util.List;

import jakarta.inject.Inject;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;
import de.timkodiert.mokka.domain.repository.CategoriesRepository;
import de.timkodiert.mokka.representation.RowType;
import de.timkodiert.mokka.util.CollectionUtils;

public class AnalysisServiceImpl implements AnalysisService {

    private final CategoriesRepository categoryRepository;

    @Inject
    public AnalysisServiceImpl(CategoriesRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<TableRowData> getTurnoverList(Reference<CategoryDTO> categoryRef, YearMonth yearMonth) {
        MonthYear monthYear = MonthYear.of(yearMonth);
        Category category = categoryRepository.findById(categoryRef.id());
        List<TableRowData> fixedTurnovers = findRelevantFixedTurnovers(category, monthYear).stream()
                                                                                           .map(turnover -> map(turnover, monthYear))
                                                                                           .toList();
        List<TableRowData> uniqueTurnovers = category.getUniqueTurnoverInformation(monthYear).
                                                     stream()
                                                     .map(UniqueTurnoverInformation::getExpense)
                                                     .distinct()
                                                     .map(this::map)
                                                     .toList();

        return CollectionUtils.union(fixedTurnovers, uniqueTurnovers);
    }

    private TableRowData map(FixedTurnover fixedTurnover, MonthYear monthYear) {
        return new TableRowData(fixedTurnover.getPosition(), fixedTurnover.getValueFor(monthYear), RowType.FIXED_EXPENSE);
    }

    private TableRowData map(UniqueTurnover turnover) {
        return new TableRowData(turnover.getBiller(), turnover.getTotalValue(), RowType.UNIQUE_EXPENSE);
    }

    private List<FixedTurnover> findRelevantFixedTurnovers(Category category, MonthYear monthYear) {
        return category.getFixedExpenses()
                       .stream()
                       .filter(turnover -> turnover.getValueFor(monthYear) != 0.0)
                       .toList();
    }
}
