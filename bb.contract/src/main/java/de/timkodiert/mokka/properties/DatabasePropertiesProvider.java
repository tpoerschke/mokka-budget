package de.timkodiert.mokka.properties;

public interface DatabasePropertiesProvider {

    static final String CIPHER = "chacha20";

    String getFullDbPath();
    String getFullDbPath(String dbPassword);
    void setDbPassphrase(String dbPassword);
}
