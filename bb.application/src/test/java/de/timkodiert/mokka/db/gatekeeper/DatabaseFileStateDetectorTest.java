package de.timkodiert.mokka.db.gatekeeper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static de.timkodiert.mokka.db.gatekeeper.DatabaseFileStateDetector.DatabaseFileState;

class DatabaseFileStateDetectorTest {

    @TempDir
    Path tempDir;

    @Test
    void detect_nonExistingFile_returnsNotFound() {
        assertEquals(DatabaseFileState.NOT_FOUND, DatabaseFileStateDetector.detect(tempDir.resolve("missing.db")));
    }

    @Test
    void detect_plainSqliteHeader_returnsPlain() throws Exception {
        Path dbFile = tempDir.resolve("plain.db");
        Files.write(dbFile, "SQLite format 3\u0000".getBytes(StandardCharsets.US_ASCII));
        assertEquals(DatabaseFileState.PLAIN, DatabaseFileStateDetector.detect(dbFile));
    }

    @Test
    void detect_encryptedHeader_returnsEncryptedOrInvalid() throws Exception {
        Path dbFile = tempDir.resolve("encrypted.db");
        Files.write(dbFile, new byte[] {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10});
        assertEquals(DatabaseFileState.ENCRYPTED_OR_INVALID, DatabaseFileStateDetector.detect(dbFile));
    }

    @Test
    void detect_fileShorterThanHeader_returnsEncryptedOrInvalid() throws Exception {
        Path dbFile = tempDir.resolve("short.db");
        Files.write(dbFile, new byte[] {0x53, 0x51, 0x4C});
        assertEquals(DatabaseFileState.ENCRYPTED_OR_INVALID, DatabaseFileStateDetector.detect(dbFile));
    }

    @Test
    void detect_jdbcPath_resolvesToFilePath() throws Exception {
        Path dbFile = tempDir.resolve("plain.db");
        Files.write(dbFile, "SQLite format 3\u0000".getBytes(StandardCharsets.US_ASCII));
        assertEquals(DatabaseFileState.PLAIN, DatabaseFileStateDetector.detect("jdbc:sqlite:" + dbFile));
    }
}
