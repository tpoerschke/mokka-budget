package de.timkodiert.mokka.monthly_overview;

import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.inject.Inject;

import de.timkodiert.mokka.domain.model.Category;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;
import de.timkodiert.mokka.domain.repository.FixedExpensesRepository;
import de.timkodiert.mokka.domain.repository.UniqueExpensesRepository;
import de.timkodiert.mokka.representation.RowType;

public class MonthlyOverviewServiceImpl implements MonthlyOverviewService {

    private final FixedExpensesRepository fixedTurnoverRepository;
    private final UniqueExpensesRepository uniqueTurnoverRepository;

    @Inject
    public MonthlyOverviewServiceImpl(FixedExpensesRepository fixedTurnoverRepository, UniqueExpensesRepository uniqueTurnoverRepository) {
        this.fixedTurnoverRepository = fixedTurnoverRepository;
        this.uniqueTurnoverRepository = uniqueTurnoverRepository;
    }

    @Override
    public MonthlyOverviewDTO generateOverview(YearMonth yearMonth) {
        List<TableRowData> fixedExpenses = loadRelevantFixedExpenses(yearMonth).stream().map(exp -> map(exp, yearMonth)).toList();
        List<TableRowData> uniqueExpenses = loadRelevantUniqueExpenses(yearMonth).stream().map(this::map).toList();
        return new MonthlyOverviewDTO(fixedExpenses, uniqueExpenses, calculateIncome(yearMonth));
    }

    private int calculateIncome(YearMonth yearMonth) {
        MonthYear monthYear = MonthYear.of(yearMonth);
        int incomeSum = fixedTurnoverRepository.findAll()
                                               .stream()
                                               .mapToInt(t -> t.getValueFor(monthYear))
                                               .filter(val -> val > 0)
                                               .sum();
        incomeSum += uniqueTurnoverRepository.findAllWithoutFixedExpense(monthYear)
                                             .stream()
                                             .mapToInt(UniqueTurnover::getTotalValue)
                                             .filter(val -> val > 0)
                                             .sum();
        return incomeSum;
    }

    private TableRowData map(FixedTurnover fixedTurnover, YearMonth yearMonth) {
        MonthYear monthYear = MonthYear.of(yearMonth);
        return new TableRowData(fixedTurnover.getId(),
                                RowType.FIXED_EXPENSE,
                                fixedTurnover.getPosition(),
                                fixedTurnover.getImportDate(monthYear),
                                fixedTurnover.getValueFor(monthYear),
                                Optional.ofNullable(fixedTurnover.getCategory()).map(Category::getName).map(List::of).orElse(List.of()),
                                fixedTurnover.hasImport(monthYear));
    }

    private TableRowData map(UniqueTurnover uniqueTurnover) {
        List<String> categories = uniqueTurnover.getPaymentInformations()
                                                .stream()
                                                .map(UniqueTurnoverInformation::getCategory)
                                                .filter(Objects::nonNull)
                                                .map(Category::getName)
                                                .distinct()
                                                .toList();
        return new TableRowData(uniqueTurnover.getId(),
                                RowType.UNIQUE_EXPENSE,
                                uniqueTurnover.getBiller(),
                                uniqueTurnover.getDate(),
                                uniqueTurnover.getTotalValue(),
                                categories,
                                uniqueTurnover.hasImport());
    }

    private List<FixedTurnover> loadRelevantFixedExpenses(YearMonth yearMonth) {
        return fixedTurnoverRepository.findAll()
                                      .stream()
                                      .filter(exp -> exp.getValueFor(MonthYear.of(yearMonth)) < 0)
                                      .toList();
    }

    private List<UniqueTurnover> loadRelevantUniqueExpenses(YearMonth yearMonth) {
        return uniqueTurnoverRepository.findAllWithoutFixedExpense(MonthYear.of(yearMonth))
                                       .stream()
                                       .filter(exp -> exp.getTotalValue() < 0)
                                       .toList();
    }
}
