package de.timkodiert.mokka.util;

import java.util.function.Function;

import org.jspecify.annotations.Nullable;

public class ObjectUtils {

    private ObjectUtils() {
    }

    public static <T> T ifNull(@Nullable T obj, T nullSubstitution) {
        return ifNull(obj, Function.identity(), nullSubstitution);
    }

    public static <T, R> R ifNull(@Nullable T obj, Function<T, R> nullSafeOperation) {
        return ifNull(obj, nullSafeOperation, null);
    }

    public static <T, R> R ifNull(@Nullable T obj, Function<T, R> nullSafeOperation, R nullSubstitution) {
        return obj == null ? nullSubstitution : nullSafeOperation.apply(obj);
    }

}
