package de.timkodiert.mokka.properties;

public interface DatabasePropertiesProvider {

    String getFullDbPath();
    String getFullDbPath(String dbPassword);
    void setDbPassphrase(String dbPassword);
}
