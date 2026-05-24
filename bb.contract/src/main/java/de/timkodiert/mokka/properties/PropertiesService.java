package de.timkodiert.mokka.properties;

import java.io.IOException;
import java.util.Properties;

import javafx.stage.Stage;

public interface PropertiesService extends DatabasePropertiesProvider {

    String DB = "db";
    String LANGUAGE = "language";
    String USE_SYSTEM_MENU_BAR = "useSystemMenuBar";
    String THEME = "theme";

    void store() throws IOException;
    void load() throws IOException;
    String getDbPath();
    String getLanguage();
    Properties getProperties();
    Stage buildWindow();
}
