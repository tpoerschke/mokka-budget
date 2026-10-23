package de.timkodiert.mokka.importer.account_turnover;

import java.text.ParseException;

import de.timkodiert.mokka.util.MoneyEssentials;

public class CsvAmountToIntegerConverter {

    private CsvAmountToIntegerConverter() {
        /* This utility class should not be instantiated */
    }

    public static Integer convert(String value) throws ImportFormatException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return MoneyEssentials.asBigDecimalFromGermanStr(value.trim()).multiply(MoneyEssentials.FACTOR_100).intValueExact();
        } catch (ParseException | NumberFormatException e) {
            throw new ImportFormatException(e);
        }
    }
}
