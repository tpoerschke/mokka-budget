package de.timkodiert.mokka.db.gatekeeper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class DatabaseFileStateDetector {

    private static final byte[] SQLITE_MAGIC = "SQLite format 3\u0000".getBytes(StandardCharsets.US_ASCII);

    public enum DatabaseFileState {
        NOT_FOUND,
        PLAIN,
        ENCRYPTED_OR_INVALID
    }

    private DatabaseFileStateDetector() {
    }

    public static DatabaseFileState detect(Path dbFile) {
        if (!Files.isRegularFile(dbFile)) {
            return DatabaseFileState.NOT_FOUND;
        }
        try (InputStream inputStream = Files.newInputStream(dbFile)) {
            byte[] header = inputStream.readNBytes(SQLITE_MAGIC.length);
            if (header.length < SQLITE_MAGIC.length) {
                return DatabaseFileState.ENCRYPTED_OR_INVALID;
            }
            return Arrays.equals(header, SQLITE_MAGIC) ? DatabaseFileState.PLAIN : DatabaseFileState.ENCRYPTED_OR_INVALID;
        } catch (IOException e) {
            return DatabaseFileState.ENCRYPTED_OR_INVALID;
        }
    }

    public static DatabaseFileState detect(String jdbcPath) {
        return detect(Path.of(DbPathHelper.toFilePath(jdbcPath)));
    }
}
