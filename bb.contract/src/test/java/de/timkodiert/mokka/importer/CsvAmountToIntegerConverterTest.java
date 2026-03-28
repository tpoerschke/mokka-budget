package de.timkodiert.mokka.importer;

import java.util.stream.Stream;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvAmountToIntegerConverterTest {

    @ParameterizedTest
    @MethodSource("validNumberFormatArguments")
    void testValidNumberFormat(String numberStr, int numberExpected)  {
        CsvAmountToIntegerConverter converter = new CsvAmountToIntegerConverter();
        int result = assertDoesNotThrow(() -> converter.convert(numberStr));
        assertEquals(numberExpected, result);
    }

    private static Stream<Arguments> validNumberFormatArguments() {
        return Stream.of(Arguments.of("0", 0),
                         Arguments.of("0,0", 0),
                         Arguments.of("0,00", 0),
                         Arguments.of("0,1", 10),
                         Arguments.of("0,10", 10),
                         Arguments.of("1", 100),
                         Arguments.of("-0,10", -10),
                         Arguments.of("-1", -100),
                         Arguments.of("-4,00", -400),
                         Arguments.of("1000,99", 100099),
                         Arguments.of("100.000,99", 10000099),
                         Arguments.of("100000,99", 10000099),
                         Arguments.of("0,123", 12),
                         Arguments.of("1,123", 112),
                         Arguments.of("100.000,556", 10000056),
                         Arguments.of("100000,556", 10000056),
                         Arguments.of("10.", 1000),
                         Arguments.of("1,", 100),
                         Arguments.of(",1", 10),
                         Arguments.of("16,90", "1690"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "a", "a1", ",", "abc,10"})
    void testInvalidNumbers(String numberStr) {
        CsvAmountToIntegerConverter converter = new CsvAmountToIntegerConverter();
        assertThrows(CsvDataTypeMismatchException.class, () -> converter.convert(numberStr));
    }
}