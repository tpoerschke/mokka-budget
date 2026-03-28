package de.timkodiert.mokka.representation;

import java.util.List;

public enum RowType {
    UNIQUE_EXPENSE, FIXED_EXPENSE, UNIQUE_EXPENSE_GROUP, FIXED_EXPENSE_GROUP, CATEGORY_GROUP, IMPORT, SUM, TOTAL_SUM, DEFAULT, EMPTY;

    public static List<RowType> getGroupTypes() {
        return List.of(UNIQUE_EXPENSE_GROUP, FIXED_EXPENSE_GROUP);
    }
}
