package de.timkodiert.mokka.converter;

import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.jspecify.annotations.Nullable;

public class ColorHexStringConverter extends StringConverter<Color> {

    @Override
    public String toString(@Nullable Color color) {
        if (color == null) {
            return null;
        }
        return String.format("#%02X%02X%02X",
                             (int) Math.round(color.getRed() * 255),
                             (int) Math.round(color.getGreen() * 255),
                             (int) Math.round(color.getBlue() * 255));
    }

    @Override
    public Color fromString(@Nullable String hexStr) {
        if (hexStr == null || hexStr.isBlank()) {
            return null;
        }
        return Color.web(hexStr);
    }
}
