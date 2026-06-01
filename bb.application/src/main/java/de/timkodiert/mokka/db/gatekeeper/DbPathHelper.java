package de.timkodiert.mokka.db.gatekeeper;

public final class DbPathHelper {

    private static final String DB_SUFFIX = ".db";
    private static final String BACKUP_SUFFIX = ".backup" + DB_SUFFIX;
    private static final String JDBC_PREFIX = "jdbc:sqlite:";

    private DbPathHelper() {
        // Statische Klasse
    }

    static String toFilePath(String jdbcPath) {
        return jdbcPath.replace(JDBC_PREFIX, "");
    }

    static String toBackupPath(String jdbcPath) {
        return jdbcPath.replace(DB_SUFFIX, BACKUP_SUFFIX);
    }
}
