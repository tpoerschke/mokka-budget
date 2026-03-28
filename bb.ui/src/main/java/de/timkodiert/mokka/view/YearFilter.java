package de.timkodiert.mokka.view;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

public class YearFilter implements ObservableValue<Integer> {

    private final ComboBox<Integer> selectedYearBox;

    private final Set<ChangeListener<? super Integer>> listeners = new HashSet<>();
    private final Set<InvalidationListener> invalidationListeners = new HashSet<>();
    private Integer value;

    public YearFilter(ComboBox<Integer> selectedYearBox, Button prevBtn, Button nextBtn, int year) {
        this.selectedYearBox = selectedYearBox;

        nextBtn.setGraphic(new FontIcon(BootstrapIcons.CHEVRON_RIGHT));
        nextBtn.setText("");
        prevBtn.setGraphic(new FontIcon(BootstrapIcons.CHEVRON_LEFT));
        prevBtn.setText("");

        this.value = year;
        selectedYearBox.getItems().setAll(IntStream.rangeClosed(value - 5, value + 5).boxed().toList());
        selectedYearBox.getSelectionModel().selectedItemProperty().addListener(this::yearBoxListener);
        setSelection(value);

        nextBtn.setOnAction(event -> setSelection(value + 1));
        prevBtn.setOnAction(event -> setSelection(value - 1));
    }

    private void yearBoxListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        value = newValue.intValue();
        this.listeners.forEach(l -> l.changed(this, oldValue.intValue(), value));
        this.invalidationListeners.forEach(l -> l.invalidated(this));
    }

    private void setSelection(Integer year) {
        selectedYearBox.getSelectionModel().select(year);
    }

    @Override
    public void addListener(ChangeListener<? super Integer> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Integer> listener) {
        listeners.remove(listener);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListeners.remove(listener);
    }
}
