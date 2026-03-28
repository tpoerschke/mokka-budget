package de.timkodiert.mokka.chart;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

import jakarta.inject.Inject;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;
import de.timkodiert.mokka.domain.repository.CategoriesRepository;
import de.timkodiert.mokka.domain.repository.UniqueExpenseInformationRepository;
import de.timkodiert.mokka.util.CollectionUtils;

public class ExpenseBreakdownServiceImpl implements ExpenseBreakdownService {

    private final UniqueExpenseInformationRepository uniqueTurnoverInformationRepository;
    private final CategoriesRepository categoriesRepository;

    @Inject
    public ExpenseBreakdownServiceImpl(UniqueExpenseInformationRepository uniqueTurnoverInformationRepository, CategoriesRepository categoriesRepository) {
        this.uniqueTurnoverInformationRepository = uniqueTurnoverInformationRepository;
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public List<ExpenseBreakdown> getExpenseBreakdown(YearMonth yearMonth) {
        List<ExpenseBreakdown> categoryBreakdowns = categoriesRepository.findAll()
                                                                        .stream()
                                                                        .map(category -> createBreakdown(category, yearMonth))
                                                                        .filter(breakdown -> breakdown.value() != 0)
                                                                        .sorted(Comparator.comparing(ExpenseBreakdown::value).reversed())
                                                                        .toList();
        int otherTurnoverSum = uniqueTurnoverInformationRepository.findAllWithoutFixedTurnoverAndCategory(MonthYear.of(yearMonth))
                                                                  .stream()
                                                                  .mapToInt(UniqueTurnoverInformation::getValueSigned)
                                                                  .sum();
        if (otherTurnoverSum < 0) {
            ExpenseBreakdown otherBreakdown = new ExpenseBreakdown(new Reference<>(CategoryDTO.class, -1, "monthlyOverview.label.others"), Math.abs(otherTurnoverSum));
            return CollectionUtils.union(categoryBreakdowns, List.of(otherBreakdown));
        }
        return categoryBreakdowns;
    }

    private ExpenseBreakdown createBreakdown(Category category, YearMonth yearMonth) {
        Reference<CategoryDTO> catRef = new Reference<>(CategoryDTO.class, category.getId(), category.getName());
        int turnoverSum = category.sumTurnoversForMonth(MonthYear.of(yearMonth));
        return new ExpenseBreakdown(catRef, Math.abs(Math.min(turnoverSum, 0)));
    }
}
