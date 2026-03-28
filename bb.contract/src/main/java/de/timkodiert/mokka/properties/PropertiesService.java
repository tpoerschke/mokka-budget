package de.timkodiert.mokka.properties;

import java.io.IOException;
import java.util.Properties;

import javafx.stage.Stage;

public interface PropertiesService {

    String DB = "db";
    String LANGUAGE = "language";
    String USE_SYSTEM_MENU_BAR = "useSystemMenuBar";
    String THEME = "theme";

    void load() throws IOException;
    String getDbPath();
    String getLanguage();
    Properties getProperties();
    Stage buildWindow();
}
