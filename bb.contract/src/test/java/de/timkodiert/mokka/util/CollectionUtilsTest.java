package de.timkodiert.mokka.util;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionUtilsTest {

    @Test
    void splitList() {
        List<String> list = List.of("str1", "str2", "str3", "str4");

        var splits = CollectionUtils.split(list, 2);

        assertEquals(List.of("str1", "str2"), splits.firstSplit());
        assertEquals(List.of("str3", "str4"), splits.secondSplit());
    }

    @Test
    void splitListHigherThanListSize() {
        List<String> list = List.of("str1", "str2", "str3", "str4");

        var splits = CollectionUtils.split(list, 4);

        assertEquals(List.of("str1", "str2", "str3", "str4"), splits.firstSplit());
        assertTrue(splits.secondSplit().isEmpty());
    }

    @Test
    void enumerateList() {
        List<String> list = List.of("str1", "str2", "str3");

        var enumeratedList = CollectionUtils.enumerate(list);

        IntStream.range(0, enumeratedList.size()).forEach(i -> {
            assertEquals(i, enumeratedList.get(i).i());
            assertEquals("str" + (i + 1), enumeratedList.get(i).value());
        });
    }

    @Test
    void enumerateEmptyList() {
        List<String> list = List.of();
        var enumeratedList = CollectionUtils.enumerate(list);
        assertTrue(enumeratedList.isEmpty());
    }
}