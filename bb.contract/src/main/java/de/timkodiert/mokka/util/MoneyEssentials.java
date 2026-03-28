package de.timkodiert.mokka.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class MoneyEssentials {


    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    public static final BigDecimal FACTOR_100 = new BigDecimal("100");
    private static final int SCALE = 2;

    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);

    private MoneyEssentials() {
        // Statische Klasse
    }

    public static BigDecimal asBigDecimal(String stringValue) {
        return new BigDecimal(stringValue).setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal asBigDecimal(int intValue) {
        return new BigDecimal(intValue).setScale(SCALE, ROUNDING_MODE);
    }

    public static BigDecimal asBigDecimalFromGermanStr(String stringValue) throws ParseException {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
        df.setParseBigDecimal(true);
        BigDecimal bd = (BigDecimal) df.parse(stringValue);
        return bd.setScale(SCALE, ROUNDING_MODE);
    }

}
