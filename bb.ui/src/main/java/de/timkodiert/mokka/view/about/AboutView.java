package de.timkodiert.mokka.view.about;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import lombok.RequiredArgsConstructor;

import de.timkodiert.mokka.exception.TechnicalException;
import de.timkodiert.mokka.view.View;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class AboutView implements View, Initializable {

    private static final String REGEX_PATTERN = "\\((.*?)\\) ([^(].*) \\(([\\w.]*:.*)\\)";

    @FXML
    private TextArea licensesArea;
    @FXML
    private Label versionLabel;

    private final HostServices hostServices;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        versionLabel.setText(getVersion());

        try (InputStream in = getClass().getResourceAsStream("/licenses/BB-THIRD-PARTY.txt")) {
            String content = new String(Objects.requireNonNull(in).readAllBytes(), StandardCharsets.UTF_8);
            licensesArea.setText(processLicenseContent(content));
        } catch (Exception e) {
            throw TechnicalException.forProgrammingError("License could not be loaded", e);
        }
    }

    @FXML
    private void openUserManualLink() {
        hostServices.showDocument("https://timkodiert.de/project/budgetbook");
    }

    @FXML
    private void openGithubLink() {
        hostServices.showDocument("https://github.com/tpoerschke/BudgetBook");
    }

    private String processLicenseContent(String content) {
        Pattern pattern = Pattern.compile(REGEX_PATTERN);
        return Arrays.stream(content.split("\n")).skip(2).map(line -> {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) {
                return "";
            }
            String licenses = "(" + matcher.group(1) + ")";
            String dependency = matcher.group(2);
            String artifact = matcher.group(3);
            return String.join(System.lineSeparator(), dependency, artifact, licenses) + System.lineSeparator();
        }).collect(Collectors.joining(System.lineSeparator()));
    }


    private String getVersion() {
        return "Version " + getClass().getPackage().getImplementationVersion();
    }
}
