package de.timkodiert.mokka.importer;

import java.text.ParseException;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import de.timkodiert.mokka.util.MoneyEssentials;

public class CsvAmountToIntegerConverter extends AbstractBeanField<Integer, String> {

    @Override
    protected Integer convert(String value) throws CsvDataTypeMismatchException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // NumberFormat nicht als Konstante, da Probleme mit Multi-Threading
            return MoneyEssentials.asBigDecimalFromGermanStr(value.trim()).multiply(MoneyEssentials.FACTOR_100).intValueExact();
        } catch (ParseException | NumberFormatException e) {
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }
}
