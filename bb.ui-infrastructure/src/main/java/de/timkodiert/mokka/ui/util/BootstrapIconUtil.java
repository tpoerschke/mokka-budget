package de.timkodiert.mokka.ui.util;

import org.jspecify.annotations.Nullable;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

public final class BootstrapIconUtil {

    private BootstrapIconUtil() {
    }

    public static @Nullable BootstrapIcons parse(@Nullable String iconName) {
        if (iconName == null || iconName.isBlank()) {
            return null;
        }
        return BootstrapIcons.valueOf(iconName);
    }

    public static @Nullable FontIcon createFontIcon(@Nullable String iconName) {
        BootstrapIcons icon = parse(iconName);
        return icon == null ? null : new FontIcon(icon);
    }

    public static @Nullable String toIconName(@Nullable BootstrapIcons icon) {
        return icon == null ? null : icon.name();
    }
}
