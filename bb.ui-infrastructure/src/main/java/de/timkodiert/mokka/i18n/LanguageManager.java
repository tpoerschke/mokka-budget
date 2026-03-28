package de.timkodiert.mokka.i18n;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;

import de.timkodiert.mokka.validation.ValidationWrapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolver;
import org.hibernate.validator.spi.messageinterpolation.LocaleResolverContext;

import de.timkodiert.mokka.properties.PropertiesService;

@Getter
@Singleton
public class LanguageManager implements LocaleResolver {

    public static final Set<Locale> AVAILABLE_LOCALES = Set.of(GERMAN, ENGLISH);
    public static final Locale DEFAULT_LOCALE = ENGLISH;

    public static final List<String> MONTH_NAMES = List.of(
        "month.january",
        "month.february",
        "month.march",
        "month.april",
        "month.may",
        "month.june",
        "month.july",
        "month.august",
        "month.september",
        "month.october",
        "month.november",
        "month.december");

    private static final String I_18_N_PACKAGE = "i18n.messages";

    private ResourceBundle fallbackRB;

    private ResourceBundle resourceBundle;

    Locale locale;

    @Inject
    public LanguageManager(PropertiesService propertiesService) {
        // Map String to Locale
        this.initialize(mapToLocale(propertiesService.getLanguage()));
    }

    private void initialize(Locale locale) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle(I_18_N_PACKAGE, locale);
        this.fallbackRB = ResourceBundle.getBundle(I_18_N_PACKAGE, ENGLISH);
    }

    /**
     * Short wrapper for <pre>LanguageManager.getInstance().getLocString(key)</pre>
     */
    public String get(String key) {
        return this.getLocString(key);
    }

    public synchronized void setLocale(Locale locale) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle(I_18_N_PACKAGE, this.locale);
    }

    public String[] getMonths() {
        return MONTH_NAMES.stream().map(this::get).toArray(String[]::new);
    }

    public String getLocString(String key) {
        if (resourceBundleContainsKey(key)) {
            return this.getResourceBundle().getString(key);
        }
        return this.getFallbackRB().getString(key);
    }


    // Intermediate solution until we have a dropdown menu for languages
    public static Locale mapToLocale(String language) {
        if (language == null) {
            return DEFAULT_LOCALE;
        }
        if (language.equalsIgnoreCase("Deutsch")) {
            return GERMAN;
        } else if (language.equalsIgnoreCase("English")) {
            return ENGLISH;
        } else {
            // Default if the language is not recognized
            return DEFAULT_LOCALE;
        }
    }

    public boolean resourceBundleContainsKey(String key) {
        return this.resourceBundle.containsKey(key);
    }

    /**
     * Verwendet von der Validierung (s. {@link ValidationWrapper}).
     *
     * @return Gibt das Kkonfigurierte Locale zurück.
     */
    @Override
    public Locale resolve(LocaleResolverContext context) {
        return locale;
    }
}
