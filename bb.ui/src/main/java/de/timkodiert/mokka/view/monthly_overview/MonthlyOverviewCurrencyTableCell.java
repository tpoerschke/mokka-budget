package de.timkodiert.mokka.view.monthly_overview;

import java.util.Map;

import javafx.beans.property.BooleanProperty;

import de.timkodiert.mokka.monthly_overview.TableRowData;
import de.timkodiert.mokka.representation.RowType;
import de.timkodiert.mokka.table.cell.CurrencyTableCell;

public class MonthlyOverviewCurrencyTableCell extends CurrencyTableCell<TableRowData, Number> {

    private final Map<RowType, BooleanProperty> isCollapsedProperties;

    public MonthlyOverviewCurrencyTableCell(Map<RowType, BooleanProperty> isCollapsedProperties) {
        super();
        this.isCollapsedProperties = isCollapsedProperties;
    }

    @Override
    protected void updateItem(Number item, boolean empty) {
        super.updateItem(item, empty);
        RowType rowType = this.getTableRow().getItem() != null ? this.getTableRow().getItem().getRowType() : null;
        if (isCollapsedProperties.containsKey(rowType) && !isCollapsedProperties.get(rowType).get()) {
            setText("");
        }
    }
}
