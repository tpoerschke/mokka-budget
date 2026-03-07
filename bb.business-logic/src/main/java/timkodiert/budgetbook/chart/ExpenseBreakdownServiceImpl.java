package timkodiert.budgetbook.chart;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

import jakarta.inject.Inject;

import timkodiert.budgetbook.domain.CategoryDTO;
import timkodiert.budgetbook.domain.Reference;
import timkodiert.budgetbook.domain.model.Category;
import timkodiert.budgetbook.domain.model.MonthYear;
import timkodiert.budgetbook.domain.model.UniqueTurnoverInformation;
import timkodiert.budgetbook.domain.repository.CategoriesRepository;
import timkodiert.budgetbook.domain.repository.UniqueExpenseInformationRepository;
import timkodiert.budgetbook.util.CollectionUtils;

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
