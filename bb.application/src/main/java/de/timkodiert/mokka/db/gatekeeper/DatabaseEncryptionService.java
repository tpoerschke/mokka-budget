package de.timkodiert.mokka.db.gatekeeper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import de.timkodiert.mokka.domain.util.EntityManager;
import de.timkodiert.mokka.properties.DatabasePropertiesProvider;
import de.timkodiert.mokka.properties.PropertiesService;

@Singleton
public class DatabaseEncryptionService {

    private final EntityManager entityManager;
    private final PropertiesService propertiesService;

    @Inject
    public DatabaseEncryptionService(EntityManager entityManager, PropertiesService propertiesService) {
        this.entityManager = entityManager;
        this.propertiesService = propertiesService;
    }

    boolean isPasswordValid(String password) {
        try (Connection connection = DriverManager.getConnection(propertiesService.getFullDbPath(password))) {
            return connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    void enableEncryption(String newPassword) throws SQLException, IOException {
        entityManager.closeSession();

        String jdbcPath = propertiesService.getDbPath();
        Path dbFile = Path.of(DbPathHelper.toFilePath(jdbcPath));
        Path backupFile = Path.of(getBackupPath());

        copyDatabaseFile(dbFile, backupFile);
        encryptDatabase(jdbcPath, newPassword);

        propertiesService.setDbPassphrase(newPassword);
        entityManager.openNewSession();
    }

    void disableEncryption(String currentPassword) throws SQLException, IOException {
        entityManager.closeSession();

        String jdbcPath = propertiesService.getDbPath();
        Path dbPath = Path.of(DbPathHelper.toFilePath(jdbcPath));
        Path backupFile = Path.of(getBackupPath());

        copyDatabaseFile(dbPath, backupFile);
        decryptDatabase(propertiesService.getFullDbPath(currentPassword));

        propertiesService.setDbPassphrase(null);
        entityManager.openNewSession();
    }

    void changePassword(String currentPassword, String newPassword) throws SQLException {
        entityManager.closeSession();
        try (Connection connection = DriverManager.getConnection(propertiesService.getFullDbPath(currentPassword));
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA rekey = '" + escapeSqlLiteral(newPassword) + "'");
        }
        propertiesService.setDbPassphrase(newPassword);
        entityManager.openNewSession();
    }

    private void encryptDatabase(String plainJdbcPath, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(plainJdbcPath);
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA cipher = '" + DatabasePropertiesProvider.CIPHER + "'");
            statement.execute("PRAGMA rekey = '" + escapeSqlLiteral(password) + "'");
        }
    }

    private void decryptDatabase(String encryptedJdbcPath) throws SQLException {
        try (Connection connection = DriverManager.getConnection(encryptedJdbcPath);
             Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA rekey = ''");
        }
    }

    private void copyDatabaseFile(Path dbPath, Path newDbPath) throws IOException {
        Files.copy(dbPath, newDbPath, StandardCopyOption.REPLACE_EXISTING);
    }

    String getBackupPath() {
        return DbPathHelper.toBackupPath(DbPathHelper.toFilePath(propertiesService.getDbPath()));
    }

    private static String escapeSqlLiteral(String value) {
        return value.replace("'", "''");
    }
}
