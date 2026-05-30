package de.timkodiert.mokka.ui.util;

import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

public final class ColorUtil {

    private ColorUtil() {
    }

    public static @Nullable Color parse(@Nullable String colorValue) {
        if (colorValue == null || colorValue.isBlank()) {
            return null;
        }
        return Color.web(colorValue);
    }

    public static @Nullable String toHex(@Nullable Color color) {
        if (color == null) {
            return null;
        }
        return String.format("#%02X%02X%02X",
                             (int) Math.round(color.getRed() * 255),
                             (int) Math.round(color.getGreen() * 255),
                             (int) Math.round(color.getBlue() * 255));
    }
}
