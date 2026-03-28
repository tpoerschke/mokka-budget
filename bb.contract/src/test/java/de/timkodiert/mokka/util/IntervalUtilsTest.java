package de.timkodiert.mokka.util;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntervalUtilsTest {

    @ParameterizedTest
    @MethodSource("overlapParams")
    void overlap(YearMonth start1, YearMonth end1, YearMonth start2, YearMonth end2, boolean expected) {
        assertEquals(expected, IntervalUtils.overlap(start1, end1, start2, end2));
    }

    private static Stream<Arguments> overlapParams() {
        return Stream.of(Arguments.of(YearMonth.of(2025, 8), YearMonth.of(2025, 10),
                                      YearMonth.of(2025, 9), YearMonth.of(2025, 11),
                                      true),
                         Arguments.of(YearMonth.of(2025, 8), null,
                                      YearMonth.of(2025, 9), YearMonth.of(2025, 11),
                                      true),
                         Arguments.of(YearMonth.of(2025, 8), YearMonth.of(2025, 10),
                                      YearMonth.of(2025, 9), null,
                                      true),
                         Arguments.of(YearMonth.of(2025, 8), null,
                                      YearMonth.of(2025, 9), null,
                                      true),
                         Arguments.of(YearMonth.of(2025, 8), YearMonth.of(2025, 10),
                                      YearMonth.of(2025, 11), YearMonth.of(2026, 1),
                                      false),
                         Arguments.of(YearMonth.of(2025, 8), YearMonth.of(2025, 10),
                                      YearMonth.of(2025, 11), null,
                                      false),
                         Arguments.of(YearMonth.of(2025, 11), null,
                                      YearMonth.of(2025, 8), YearMonth.of(2025, 10),
                                      false));
    }

    @Test
    void yearMonthRange() {
        // Arrange
        YearMonth start = YearMonth.of(2025, 10);
        YearMonth end = YearMonth.of(2026, 2);

        // Act
        List<YearMonth> range = IntervalUtils.yearMonthRange(start, end);

        // Assert
        assertEquals(5, range.size());
        assertEquals(YearMonth.of(2025, 10), range.getFirst());
        assertEquals(YearMonth.of(2025, 11), range.get(1));
        assertEquals(YearMonth.of(2025, 12), range.get(2));
        assertEquals(YearMonth.of(2026, 1), range.get(3));
        assertEquals(YearMonth.of(2026, 2), range.get(4));
    }
}