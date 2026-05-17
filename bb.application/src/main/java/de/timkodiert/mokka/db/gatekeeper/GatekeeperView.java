package de.timkodiert.mokka.db.gatekeeper;

import java.net.URL;
import java.util.ResourceBundle;

import atlantafx.base.theme.Styles;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.view.View;

public class GatekeeperView implements View, Initializable {

    @FXML
    private Hyperlink noDataEncLink;
    @FXML
    private Label passwortLossLabel;

    @Inject
    public  GatekeeperView() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        noDataEncLink.setVisited(true); // für Styling
        noDataEncLink.setTooltip(new Tooltip("Die Verschlüsselung kann auch nachträglich aktiviert werden."));

        passwortLossLabel.setGraphic(new FontIcon(BootstrapIcons.EXCLAMATION_CIRCLE));
        passwortLossLabel.getStyleClass().add(Styles.ACCENT);
        passwortLossLabel.setGraphicTextGap(10);
    }
}
