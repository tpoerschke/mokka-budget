package de.timkodiert.mokka.view;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;


@AssistedFactory
public interface MonthFilterFactory {
    MonthFilter create(@Assisted("selectedMonthBox") ComboBox<String> selectedMonthBox,
                       @Assisted("selectedYearBox") ComboBox<Integer> selectedYearBox,
                       @Assisted("nextBtn") Button nextBtn,
                       @Assisted("prevBtn") Button prevBtn);
}
