package de.timkodiert.mokka.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class CollectionUtils {

    public static <T> List<T> union(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>();
        result.addAll(list1);
        result.addAll(list2);
        return Collections.unmodifiableList(result);
    }

    /**
     * Teilt {@code list} bei {@code splitIndex} in zwei Teillisten ({@code [0, splitIndex)} und {@code [splitIndex, size)}).
     * Liegt der Index am Ende oder darüber, ist {@code secondSplit} leer.
     *
     * @return {@code Splits} mit den beiden Teillisten
     * @throws IllegalArgumentException wenn {@code splitIndex} negativ ist
     */
    public static <T> Splits<T> split(List<T> list, int splitIndex) {
        if (splitIndex < 0) {
            throw new IllegalArgumentException("splitIndex must be greater than 0");
        }
        if (splitIndex > list.size() - 1) {
            return new Splits<>(Collections.unmodifiableList(list), List.of());
        }
        return new Splits<>(list.subList(0, splitIndex), list.subList(splitIndex, list.size()));
    }

    public record Splits<T>(List<T> firstSplit, List<T> secondSplit) {}

    /**
     * Zählt eine Liste beginnend mit 0 durch.
     *
     * @param list Die Liste, die durchgezählt werden soll
     * @return Tuple {@code IndexValue}, das das Listenelement mit seinem Index in der gegebenen Liste enthält
     */
    public static <T> List<IndexValue<T>> enumerate(List<T> list) {
        return IntStream.range(0, list.size()).mapToObj(i -> new IndexValue<>(i, list.get(i))).toList();
    }

    public record IndexValue<T>(int i, T value) {}
}
