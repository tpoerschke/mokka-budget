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
