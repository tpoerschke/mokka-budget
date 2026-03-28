package de.timkodiert.mokka.converter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.util.StringConverter;

import de.timkodiert.mokka.analysis.AnalysisPeriod;
import de.timkodiert.mokka.budget.BudgetType;
import de.timkodiert.mokka.domain.CategoryGroupDTO;
import de.timkodiert.mokka.domain.PaymentType;
import de.timkodiert.mokka.domain.TurnoverDirection;
import de.timkodiert.mokka.i18n.LanguageManager;

@Singleton
public class Converters {

    private static final Map<Class<?>, StringConverter<?>> CONVERTER_MAP = new HashMap<>();

    private final LanguageManager languageManager;

    @Inject
    public Converters(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    public void register() {
        CONVERTER_MAP.put(LocalDate.class, new LocalDateStringConverter());
        CONVERTER_MAP.put(TurnoverDirection.class, new EnumStringConverter<>(languageManager));
        CONVERTER_MAP.put(PaymentType.class, new EnumStringConverter<>(languageManager));
        CONVERTER_MAP.put(CategoryGroupDTO.class, new CategoryGroupStringConverter());
        CONVERTER_MAP.put(BudgetType.class, new EnumStringConverter<>(languageManager));
        CONVERTER_MAP.put(AnalysisPeriod.class, new EnumStringConverter<>(languageManager));
    }

    public static <T> StringConverter<T> get(Class<T> type) {
        return (StringConverter<T>) CONVERTER_MAP.get(type);
    }
}
