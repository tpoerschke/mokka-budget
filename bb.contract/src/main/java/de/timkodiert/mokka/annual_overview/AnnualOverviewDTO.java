package de.timkodiert.mokka.annual_overview;

import java.util.List;

public record AnnualOverviewDTO(List<TableRowData> expensesRowData, TableRowData earningsSum, TableRowData expensesSum, TableRowData totalSum) {
}
