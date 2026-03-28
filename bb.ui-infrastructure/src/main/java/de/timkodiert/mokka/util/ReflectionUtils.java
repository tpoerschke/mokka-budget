package de.timkodiert.mokka.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

public class ReflectionUtils {

    private ReflectionUtils() {
        // Utility Class
    }

    public static String resolveMethodName(SerializableFunction<?, ?> function) throws ReflectiveOperationException {
        return resolveMethod(function).getName();
    }

    public static Method getMethodForName(Class<?> clazz, String methodName) throws NoSuchMethodException {
        return Arrays.stream(clazz.getDeclaredMethods())
                     .filter(method -> method.getName().equals(methodName))
                     .findAny()
                     .orElseThrow(() -> new NoSuchMethodException("Method not found: " + methodName));
    }

    public static Method resolveMethod(SerializableFunction<?, ?> function) throws ReflectiveOperationException {
        SerializedLambda serializedLambda = extractLambda(function);
        String className = serializedLambda.getImplClass().replace('/', '.');
        String methodName = serializedLambda.getImplMethodName();

        return getMethodForName(Class.forName(className), methodName);
    }

    private static SerializedLambda extractLambda(SerializableFunction<?, ?> function) throws ReflectiveOperationException {
        Method writeReplace = function.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        return (SerializedLambda) writeReplace.invoke(function);
    }

    @FunctionalInterface
    public interface SerializableFunction<B, R> extends Function<B, R>, Serializable {}
}
