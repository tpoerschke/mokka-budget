package de.timkodiert.mokka.logging;

import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

public final class DatabaseLogSanitizer {

    private static final Pattern DB_KEY_IN_URL = Pattern.compile("([&?]key=)[^&\\s\"']*", Pattern.CASE_INSENSITIVE);

    private DatabaseLogSanitizer() {
    }

    static String sanitize(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            return message == null ? "" : message;
        }
        return DB_KEY_IN_URL.matcher(message).replaceAll("$1***");
    }
}
