package de.timkodiert.mokka.ui.helper;

import java.util.function.Consumer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class OneTimeListener<T> implements ChangeListener<T> {

    private final Consumer<T> action;

    private OneTimeListener(ObservableValue<T> observable, Consumer<T> action) {
        this.action = action;
        observable.addListener(this);
    }

    public static <T> void on(ObservableValue<T> observable, Consumer<T> action) {
        new OneTimeListener<>(observable, action);
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        action.accept(newValue);
        observable.removeListener(this);
    }
}
