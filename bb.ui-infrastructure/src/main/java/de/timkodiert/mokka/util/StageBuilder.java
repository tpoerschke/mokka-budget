package de.timkodiert.mokka.util;

import java.io.IOException;
import javax.inject.Inject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import de.timkodiert.mokka.view.View;

public class StageBuilder {

    private final FXMLLoader fxmlLoader;

    private Window owner;
    private Modality modality;
    private String resourcePath;
    private View viewController;    
    private String title;
    private Integer width;
    private Integer height;

    @Inject
    public StageBuilder(FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    public StageBuilder withModality(Modality modality) {
        this.modality = modality;
        return this;
    }

    public StageBuilder withOwner(Window owner) {
        this.owner = owner;
        return this;
    }

    public StageBuilder withFXMLResource(String resourcePath)  {
        this.resourcePath = resourcePath;
        return this;
    }

    public StageBuilder withView(View viewController) {
        this.viewController = viewController;
        return this;
    }

    public StageBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public StageBuilder minSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public StageTuple build() throws IOException {
        fxmlLoader.setLocation(getClass().getResource(resourcePath));
        fxmlLoader.setController(viewController);

        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("/css/general-styles.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.initModality(modality);
        stage.initOwner(owner);
        if (width != null && height != null) {
            stage.setMinWidth(width);
            stage.setMinHeight(height);
        }

        return new StageTuple(stage, fxmlLoader.getController());
    }

    public record StageTuple(Stage stage, View view) {}
}
