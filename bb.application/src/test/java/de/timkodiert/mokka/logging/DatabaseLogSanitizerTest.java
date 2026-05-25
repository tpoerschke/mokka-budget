package de.timkodiert.mokka.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseLogSanitizerTest {

    @Test
    void sanitize_null_returnsEmptyString() {
        assertEquals("", DatabaseLogSanitizer.sanitize(null));
    }

    @Test
    void sanitize_emptyString_returnsEmptyString() {
        assertEquals("", DatabaseLogSanitizer.sanitize(""));
    }

    @Test
    void sanitize_messageWithoutKey_isUnchanged() {
        assertEquals("Could not connect to database.", DatabaseLogSanitizer.sanitize("Could not connect to database."));
    }

    @Test
    void sanitize_jdbcUrlWithKey_redactsKeyValue() {
        String message = "opening db 'jdbc:sqlite:/home/user/data.enc.db?cipher=ChaCha20&key=secret123': file is not a database";
        assertEquals("opening db 'jdbc:sqlite:/home/user/data.enc.db?cipher=ChaCha20&key=***': file is not a database",
                     DatabaseLogSanitizer.sanitize(message));
    }

    @Test
    void sanitize_stackTraceWithKeyInCauseMessage_redactsKeyValue() {
        String stackTrace = """
                java.sql.SQLException: opening db 'jdbc:sqlite:test.enc.db?cipher=ChaCha20&key=myPass'
                    at org.sqlite.SQLiteConnection.open(SQLiteConnection.java:261)
                """;
        assertEquals("""
                java.sql.SQLException: opening db 'jdbc:sqlite:test.enc.db?cipher=ChaCha20&key=***'
                    at org.sqlite.SQLiteConnection.open(SQLiteConnection.java:261)
                """, DatabaseLogSanitizer.sanitize(stackTrace));
    }
}
