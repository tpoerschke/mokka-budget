package de.timkodiert.mokka.annual_overview;

import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import de.timkodiert.mokka.domain.CategoryDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.domain.TurnoverDirection;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;
import de.timkodiert.mokka.domain.repository.FixedExpensesRepository;
import de.timkodiert.mokka.domain.repository.UniqueExpensesRepository;
import de.timkodiert.mokka.representation.RowType;

public class AnnualOverviewServiceImpl implements AnnualOverviewService {

    private static final String LABEL_EXPENSES = "annualOverview.label.expenses";
    private static final String LABEL_EARNINGS = "annualOverview.label.earnings";
    private static final String LABEL_OTHERS = "annualOverview.label.others";
    private static final String LABEL_TOTAL = "annualOverview.label.total";

    private final FixedExpensesRepository fixedTurnoverRepository;
    private final UniqueExpensesRepository uniqueTurnoverRepository;

    @Inject
    public AnnualOverviewServiceImpl(FixedExpensesRepository fixedTurnoverRepository, UniqueExpensesRepository uniqueTurnoverRepository) {
        this.fixedTurnoverRepository = fixedTurnoverRepository;
        this.uniqueTurnoverRepository = uniqueTurnoverRepository;
    }

    @Override
    public AnnualOverviewDTO generateOverview(int year) {
        List<FixedTurnover> fixedExpenses = loadAllRelevantFixedExpenses();
        List<UniqueTurnoverInformation> uniqueTurnoverInformationList = loadAllRelevantUniqueExpenseInformation(year);

        List<TableRowData> ftTableRowDataList = fixedExpenses.stream().map(t -> convertToRowData(t, year)).toList();
        List<TableRowData> utTableRowDataList = convertToRowData(uniqueTurnoverInformationList);

        TableRowData earningsSum = new TableRowData(-1, LABEL_EARNINGS, new HashMap<>(), null, RowType.SUM);
        sumTurnovers(earningsSum, loadAllRelevantFixedIncomes(), year);
        sumTurnovers(earningsSum, loadAllRelevantUniqueIncomeInformation(year));

        TableRowData expensesSum = new TableRowData(-1, LABEL_EXPENSES, new HashMap<>(), null, RowType.SUM);
        sumTurnovers(expensesSum, fixedExpenses, year);
        sumTurnovers(expensesSum, uniqueTurnoverInformationList);

        TableRowData totalSum = sumRowData(earningsSum, expensesSum);

        return new AnnualOverviewDTO(Stream.concat(ftTableRowDataList.stream(), utTableRowDataList.stream()).toList(), earningsSum, expensesSum, totalSum);
    }

    private TableRowData sumRowData(TableRowData... rowData) {
        Map<Integer, Integer> totalSumMap = new HashMap<>();
        Arrays.stream(rowData).forEach(rd -> {
            for (Month month : Month.values()) {
                Integer value = rd.monthValueMap().get(month.getValue());
                if (value != null) {
                    totalSumMap.merge(month.getValue(), value, Integer::sum);
                }
            }
        });
        return new TableRowData(-1, LABEL_TOTAL, totalSumMap, null, RowType.SUM);
    }

    private void sumTurnovers(TableRowData target, List<FixedTurnover> turnovers, int year) {
        turnovers.forEach(turnover -> {
            for (Month month : Month.values()) {
                target.monthValueMap().merge(month.getValue(), turnover.getValueFor(MonthYear.of(month.getValue(), year)), Integer::sum);
            }
        });
    }

    private void sumTurnovers(TableRowData target, List<UniqueTurnoverInformation> infoList) {
        infoList.forEach(info -> {
            int month = info.getExpense().getDate().getMonthValue();
            target.monthValueMap().merge(month, info.getValueSigned(), Integer::sum);
        });
    }

    private TableRowData convertToRowData(FixedTurnover turnover, int year) {
        Map<Integer, Integer> monthValueMap = new HashMap<>();
        for (Month month : Month.values()) {
            monthValueMap.put(month.getValue(), turnover.getValueFor(MonthYear.of(month.getValue(), year)));
        }
        Reference<CategoryDTO> categoryReference = Optional.ofNullable(turnover.getCategory())
                                                           .map(c -> new Reference<>(CategoryDTO.class, c.getId(), c.getName()))
                                                           .orElseGet(() -> new Reference<>(CategoryDTO.class, Integer.MAX_VALUE, LABEL_OTHERS));
        return new TableRowData(turnover.getId(), turnover.getPosition(), monthValueMap, categoryReference, RowType.FIXED_EXPENSE);
    }

    private List<TableRowData> convertToRowData(List<UniqueTurnoverInformation> infoList) {
        Map<Reference<CategoryDTO>, Map<Integer, Integer>> categoryMonthValueMap = new HashMap<>();
        infoList.forEach(info -> {
            Reference<CategoryDTO> category = createCategoryFromTurnoverInformation(info);
            int month = info.getExpense().getDate().getMonthValue();
            var monthValueMap = categoryMonthValueMap.computeIfAbsent(category, k -> new HashMap<>());
            monthValueMap.put(month, monthValueMap.getOrDefault(month, 0) + info.getValueSigned());
        });
        return categoryMonthValueMap.entrySet()
                                    .stream()
                                    .map(entry -> new TableRowData(-1, LABEL_OTHERS, entry.getValue(), entry.getKey(), RowType.UNIQUE_EXPENSE))
                                    .toList();
    }

    private Reference<CategoryDTO> createCategoryFromTurnoverInformation(UniqueTurnoverInformation info) {
        return Optional.ofNullable(info.getCategory())
                       .map(c -> new Reference<>(CategoryDTO.class, c.getId(), c.getName()))
                       .orElseGet(() -> new Reference<>(CategoryDTO.class, Integer.MAX_VALUE, LABEL_OTHERS));
    }

    private List<FixedTurnover> loadAllRelevantFixedExpenses() {
        return fixedTurnoverRepository.findAll().stream().filter(turnover -> turnover.getDirection() == TurnoverDirection.OUT).toList();
    }

    private List<FixedTurnover> loadAllRelevantFixedIncomes() {
        return fixedTurnoverRepository.findAll().stream().filter(turnover -> turnover.getDirection() == TurnoverDirection.IN).toList();
    }

    private List<UniqueTurnoverInformation> loadAllRelevantUniqueExpenseInformation(int year) {
        return uniqueTurnoverRepository.findAllWithoutFixedExpense(year)
                                       .stream()
                                       .map(UniqueTurnover::getPaymentInformations)
                                       .flatMap(Collection::stream)
                                       .filter(info -> info.getDirection() == TurnoverDirection.OUT)
                                       .toList();
    }

    private List<UniqueTurnoverInformation> loadAllRelevantUniqueIncomeInformation(int year) {
        return uniqueTurnoverRepository.findAllWithoutFixedExpense(year)
                                       .stream()
                                       .map(UniqueTurnover::getPaymentInformations)
                                       .flatMap(Collection::stream)
                                       .filter(info -> info.getDirection() == TurnoverDirection.IN)
                                       .toList();
    }
}
