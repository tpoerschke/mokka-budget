package de.timkodiert.mokka.view.unique_turnover;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.view.View;

public class ImageModalView implements View, Initializable {

    @FXML
    private VBox root;
    @FXML
    private Label pathLabel;
    @FXML
    private ImageView imageView;

    private final String path;

    private final LanguageManager languageManager;

    public ImageModalView(LanguageManager languageManager, String path) {
        this.languageManager = languageManager;
        this.path = path;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pathLabel.setText(languageManager.get("imageView.label.file") + path);
        imageView.setImage(new Image(new File(path).toURI().toString()));
        imageView.fitWidthProperty().bind(root.widthProperty());
        imageView.fitHeightProperty().bind(root.heightProperty().subtract(pathLabel.heightProperty()));
    }

}
