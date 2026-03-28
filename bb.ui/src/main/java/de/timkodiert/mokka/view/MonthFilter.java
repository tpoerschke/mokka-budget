package de.timkodiert.mokka.view;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import de.timkodiert.mokka.i18n.LanguageManager;

public class MonthFilter implements ObservableValue<YearMonth> {

    private final ComboBox<String> selectedMonthBox;
    private final ComboBox<Integer> selectedYearBox;

    private final Set<ChangeListener<? super YearMonth>> listeners = new HashSet<>();
    private final Set<InvalidationListener> invalidationListeners = new HashSet<>();
    private YearMonth value;

    @AssistedInject
    public MonthFilter(LanguageManager languageManager,
                       @Assisted("selectedMonthBox") ComboBox<String> selectedMonthBox,
                       @Assisted("selectedYearBox") ComboBox<Integer> selectedYearBox,
                       @Assisted("nextBtn") Button nextBtn,
                       @Assisted("prevBtn") Button prevBtn) {
        this.selectedMonthBox = selectedMonthBox;
        this.selectedYearBox = selectedYearBox;

        nextBtn.setGraphic(new FontIcon(BootstrapIcons.CHEVRON_RIGHT));
        nextBtn.setText("");
        prevBtn.setGraphic(new FontIcon(BootstrapIcons.CHEVRON_LEFT));
        prevBtn.setText("");

        this.value = YearMonth.now();
        selectedMonthBox.getItems().setAll(languageManager.getMonths());
        selectedYearBox.getItems().setAll(IntStream.rangeClosed(value.getYear() - 5, value.getYear() + 5).boxed().toList());
        selectedMonthBox.getSelectionModel().selectedIndexProperty().addListener(this::monthBoxListener);
        selectedYearBox.getSelectionModel().selectedItemProperty().addListener(this::yearBoxListener);
        setSelection(value);

        nextBtn.setOnAction(event -> setSelection(value.plusMonths(1)));
        prevBtn.setOnAction(event -> setSelection(value.plusMonths(-1)));
    }

    private void monthBoxListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        YearMonth oldMonthYear = value;
        value = YearMonth.of(value.getYear(), newValue.intValue() + 1);
        this.listeners.forEach(l -> l.changed(this, oldMonthYear, value));
        this.invalidationListeners.forEach(l -> l.invalidated(this));
    }

    private void yearBoxListener(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        YearMonth oldMonthYear = value;
        value = YearMonth.of(newValue.intValue(), value.getMonth());
        this.listeners.forEach(l -> l.changed(this, oldMonthYear, value));
        this.invalidationListeners.forEach(l -> l.invalidated(this));
    }

    private void setSelection(YearMonth monthYear) {
        selectedMonthBox.getSelectionModel().select(monthYear.getMonthValue() - 1);
        selectedYearBox.getSelectionModel().select(Integer.valueOf(monthYear.getYear()));
    }

    @Override
    public void addListener(ChangeListener<? super YearMonth> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super YearMonth> listener) {
        listeners.remove(listener);
    }

    @Override
    public YearMonth getValue() {
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
