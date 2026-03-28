package de.timkodiert.mokka.view;

import javax.inject.Inject;

import javafx.fxml.FXMLLoader;

import de.timkodiert.mokka.i18n.LanguageManager;
import de.timkodiert.mokka.injector.ControllerFactory;

public class BbFxmlLoader extends FXMLLoader {

    @Inject
    public BbFxmlLoader(ControllerFactory controllerFactory, LanguageManager languageManager) {
        super();
        setControllerFactory(controllerFactory::create);
        setResources(languageManager.getResourceBundle());
    }
}
