package de.timkodiert.mokka.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import de.timkodiert.mokka.Constants;
import de.timkodiert.mokka.i18n.LanguageManager;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

@Singleton
public class PropertiesServiceImpl implements PropertiesService {

    private final Provider<LanguageManager> languageManager; // Provider, da sonst eine zyklische Abhängigkeit entsteht

    @Getter
    private Properties properties;

    @Getter
    @Setter
    private OperationMode operationMode = OperationMode.PRODUCTION;

    private List<ThemeOption> themeComboBoxItems;

    @Inject
    public PropertiesServiceImpl(Provider<LanguageManager> languageManager) {
        this.languageManager = languageManager;
        this.properties = new Properties();
    }

    @Override
    public void load() throws IOException {
        File propsFile = Path.of(getPropertiesPath()).toFile();
        if (!propsFile.exists()) {
            Path.of(propsFile.getParent()).toFile().mkdirs();
            propsFile.createNewFile();
            this.properties.setProperty(DB, "jdbc:sqlite:"
                    + Path.of(System.getProperty("user.home"), Constants.DATA_DIR, "sqlite.db").toString());
            this.properties.setProperty(USE_SYSTEM_MENU_BAR, "true");
            this.properties.setProperty(LANGUAGE, "Deutsch");
            this.properties.setProperty(THEME, "0");
            try (FileWriter fileWriter = new FileWriter(propsFile)) {
                this.properties.store(fileWriter, "Store initial props");
            }
        } else {
            try (FileInputStream fis = new FileInputStream(getPropertiesPath())) {
                this.properties.load(fis);
            }
        }
        initializeDataStructures();
    }

    private void initializeDataStructures() {
        themeComboBoxItems = List.of(
                new ThemeOption("0", languageManager.get().get("settings.comboItem.light"), PrimerLight.class),
                new ThemeOption("1", languageManager.get().get("settings.comboItem.dark"), PrimerDark.class),
                new ThemeOption("2", languageManager.get().get("settings.comboItem.auto"), null)
        );
    }

    public void verifySettings() throws IOException {
        // TODO
        // Throw exceptions if settings are broken and offer a regeneration of the settings file.
    }

    public Stage buildWindow() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // language dropdown
        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll("Deutsch", "English");
        languageComboBox.setPromptText(languageManager.get().get("settings.prompt.selectLanguage")); // replace label according to current language
        languageComboBox.setValue(properties.getProperty(LANGUAGE));
        grid.add(new Label(languageManager.get().get("settings.label.language")), 0, 0);
        grid.add(languageComboBox, 1, 0);

        // DB JDBC Path TextField
        TextField jdbcPathTextField = new TextField();
        jdbcPathTextField.setPromptText("Enter JDBC Path");
        jdbcPathTextField.setText(properties.getProperty(DB));
        grid.add(new Label("DB JDBC Path:"), 0, 1);
        grid.add(jdbcPathTextField, 1, 1);

        // Use System Menu Bar CheckBox
        CheckBox useSystemMenuBarCheckBox = new CheckBox("Use System Menu Bar");
        useSystemMenuBarCheckBox.setSelected(Boolean.parseBoolean(properties.getProperty(USE_SYSTEM_MENU_BAR)));
        grid.add(useSystemMenuBarCheckBox, 0, 2, 2, 1);

        ComboBox<ThemeOption> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll(themeComboBoxItems);
        themeComboBox.setPromptText(languageManager.get().get("settings.prompt.selectTheme"));
        themeComboBox.setValue(getCurrentThemeOption());
        themeComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ThemeOption themeOption) {
                return themeOption != null ? themeOption.getName() : null;
            }
            @Override
            public ThemeOption fromString(String string) {
                return null;
            }
        });
        grid.add(new Label(languageManager.get().get("settings.label.theme")), 0, 3);
        grid.add(themeComboBox, 1, 3);


        Button saveBtn = new Button(languageManager.get().get("button.save"));
        saveBtn.setOnAction(event -> {
            try {
                Properties newProps = new Properties();
                newProps.setProperty(LANGUAGE, languageComboBox.getValue());
                newProps.setProperty(DB, jdbcPathTextField.getText());
                newProps.setProperty(USE_SYSTEM_MENU_BAR, String.valueOf(useSystemMenuBarCheckBox.isSelected()));
                newProps.setProperty(THEME, themeComboBox.getValue().getId());
                // Speichern
                File propsFile = Path.of(getPropertiesPath()).toFile();
                this.properties = newProps;
                this.properties.store(new FileWriter(propsFile), "MOKKA Budget properties");
                Alert alert = new Alert(AlertType.INFORMATION, languageManager.get().get("settings.alert.savedPleaseRestart"));
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR, languageManager.get().get("settings.alert.saveError"));
                alert.showAndWait();
            }
        });

        HBox buttonBox = new HBox(saveBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        BorderPane root = new BorderPane();
        root.setCenter(grid);
        root.setBottom(buttonBox);
        root.setPadding(new Insets(20));

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 500, 300));
        stage.setTitle(languageManager.get().get("settings.stage.title"));
        return stage;
    }

    private ThemeOption getCurrentThemeOption() {
        return themeComboBoxItems.stream()
                                 .filter(e -> e.getId().equals(properties.getProperty(THEME)))
                                 .findFirst()
                                 .orElse(themeComboBoxItems.getFirst());
    }

    public Class<? extends Theme> getTheme() {
        return getCurrentThemeOption().getTheme();
    }

    @Override
    public String getDbPath() {
        return properties.getProperty(DB);
    }

    @Override
    public String getLanguage() {
        return properties.getProperty(LANGUAGE);
    }

    private String getPropertiesPath() {
        return String.format(Constants.PROPERTIES_PATH_TEMPLATE, operationMode.getPropertiesPostfix());
    }
}

