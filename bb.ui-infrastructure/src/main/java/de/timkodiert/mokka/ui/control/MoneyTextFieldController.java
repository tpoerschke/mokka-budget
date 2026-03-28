package de.timkodiert.mokka.ui.control;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.exception.TechnicalException;
import de.timkodiert.mokka.util.MoneyEssentials;

import static de.timkodiert.mokka.util.MoneyEssentials.ROUNDING_MODE;
import static de.timkodiert.mokka.util.MoneyEssentials.ZERO;
import static de.timkodiert.mokka.util.MoneyEssentials.asBigDecimal;
import static de.timkodiert.mokka.util.ObjectUtils.nvl;

class MoneyTextFieldController {

    private static final Pattern VALID_PATTERN = Pattern.compile("\\d+,\\d\\d");

    @Getter
    private final DecimalFormat format = new DecimalFormat("0.00");

    private final StringProperty stringValue = new SimpleStringProperty("0,00");
    private final ObjectProperty<Integer> integerValue = new SimpleObjectProperty<>(0); // Keine IntegerProperty, da null-Werte nötig
    @Setter
    private boolean nullable;

    private boolean mute = false;

    public MoneyTextFieldController() {
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.GERMAN));

        stringValue.addListener((observable, oldValue, newValue) -> {
            if (mute) {
                return;
            }
            mute = true;
            integerValue.setValue(nvl(getValue(), v -> v.multiply(MoneyEssentials.FACTOR_100).intValueExact()));
            mute = false;
        });

        integerValue.addListener((observable, oldValue, newValue) -> {
            if (mute) {
                return;
            }
            mute = true;
            if (newValue == null) {
                setValue(nullable ? null : ZERO);
            } else {
                setValue(asBigDecimal(newValue).divide(MoneyEssentials.FACTOR_100, ROUNDING_MODE));
            }
            mute = false;
        });
    }

    public StringProperty stringValueProperty() {
        return stringValue;
    }

    public void setValue(@Nullable BigDecimal value) {
        if (value == null) {
            stringValue.set(null);
        } else {
            stringValue.set(format.format(value));
        }
    }

    @Nullable BigDecimal getValue() {
        String value = stringValue.get();
        if (StringUtils.isEmpty(value) || !isStringFormatValid()) {
            return nullable ? null : ZERO;
        }
        try {
            return asBigDecimal(value.replace(",", "."));
        } catch (NumberFormatException e) {
            throw TechnicalException.forProgrammingError(e.getMessage(), e);
        }
    }

    public boolean isStringFormatValid() {
        String value = stringValue.get();
        if (StringUtils.isEmpty(value)) {
            return nullable;
        }
        return VALID_PATTERN.matcher(value.trim()).matches();
    }

    public ObjectProperty<Integer> integerValueProperty() {
        return integerValue;
    }
}
