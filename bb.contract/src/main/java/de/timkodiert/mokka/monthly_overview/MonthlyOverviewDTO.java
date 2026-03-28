package de.timkodiert.mokka.monthly_overview;

import java.util.List;
import java.util.stream.Stream;

public record MonthlyOverviewDTO(List<TableRowData> fixedExpenses, List<TableRowData> uniqueExpenses, int incomeSum) {

    public int totalSumExpenses() {
        return Stream.concat(fixedExpenses.stream(), uniqueExpenses.stream()).mapToInt(TableRowData::value).sum();
    }
}
