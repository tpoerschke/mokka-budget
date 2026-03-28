package de.timkodiert.mokka.domain.model;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonthYearTest {

    @Test
    void testRange() {
        // Arrange
        MonthYear from = MonthYear.of(1, 2025);
        MonthYear to = MonthYear.of(4, 2025);

        // Act
        List<MonthYear> range = MonthYear.range(from, to);

        // Assert
        assertEquals(4, range.size());
        IntStream.range(0, 4).forEach(i -> assertEquals(MonthYear.of(i + 1, 2025), range.get(i)));
    }

}