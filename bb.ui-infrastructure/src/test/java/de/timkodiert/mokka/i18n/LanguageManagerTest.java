package de.timkodiert.mokka.i18n;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Stream;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.timkodiert.mokka.properties.PropertiesService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LanguageManagerTest {

    private LanguageManager sut;

    @BeforeEach
    void before() {
        sut = new LanguageManager(new EmptyPropertiesService());
    }

    static Stream<Arguments> testArgs(){
        return Stream.of(
                Arguments.of(GERMAN, "Willkommen", "Deutsch"),
                Arguments.of(ENGLISH, "Welcome", "English")
        );
    }

    @ParameterizedTest
    @MethodSource("testArgs")
    void should_return_the_string_for_specified_language_and_key(Locale locale, String expectedString){
        String greetingKey = "main.greeting";
        sut.setLocale(locale); // Setting the locale explicitly because in test context, the loaded property will be null.
        assertEquals(expectedString, sut.getLocString(greetingKey));
    }

    @Test
    void should_use_the_fallback_ResourceBundle_if_current_one_does_not_contain_key(){
        sut.setLocale(GERMAN);
        assertEquals("Fallback", sut.getLocString("testing.fallback"));
    }

    static class EmptyPropertiesService implements PropertiesService {

        @Override
        public void load() throws IOException {
            // Fake-Impl
        }

        @Override
        public String getDbPath() {
            return null;
        }

        @Override
        public String getLanguage() {
            return null;
        }

        @Override
        public Properties getProperties() {
            return null;
        }

        @Override
        public Stage buildWindow() {
            return null;
        }
    }
}