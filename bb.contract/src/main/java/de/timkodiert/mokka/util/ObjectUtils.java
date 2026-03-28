package de.timkodiert.mokka.util;

import java.util.function.Function;

import org.jspecify.annotations.Nullable;

public class ObjectUtils {

    private ObjectUtils() {
    }

    public static <T, R> R nvl(@Nullable T obj, Function<T, R> nullSafeOperation) {
        return nvl(obj, nullSafeOperation, null);
    }

    public static <T, R> R nvl(@Nullable T obj, Function<T, R> nullSafeOperation, R nullSubstitution) {
        return obj == null ? nullSubstitution : nullSafeOperation.apply(obj);
    }

}
