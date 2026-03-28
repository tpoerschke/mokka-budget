package de.timkodiert.mokka.properties;

import java.io.IOException;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import lombok.AllArgsConstructor;
import lombok.Data;

// IDEA: USE ENUM FOR THEMES LIKE E.G. => LIGHT(PrimerLight.class)
@Data
@AllArgsConstructor
public class ThemeOption {
    private String id;
    private String name;
    private Class<? extends Theme> theme;

    public Class<? extends Theme> getTheme(){
        if(this.theme == null){ // If auto mode
            OperatingSystem os = OperatingSystem.get();
            // OS-dependent decision making
            if (os.equals(OperatingSystem.WINDOWS)) {
                return fromWindowsTheme();
            } else if (os.equals(OperatingSystem.OSX)) {
                return fromOsxTheme();
            } else if (os.equals(OperatingSystem.LINUX)) {
                return fromLinuxTheme();
            }
            // If no known OS could be detected => use fallback
            return PrimerLight.class;
        }
        // If no auto-theme => use theme for the selected mode
        return this.theme;
    }


    private Class<? extends Theme> fromOsxTheme() {
        try {
            Process process = Runtime.getRuntime().exec("defaults read -g AppleInterfaceStyle");
            process.waitFor();
            String output = new String(process.getInputStream().readAllBytes()).trim();
            if (output.equals("Dark")) {
                return PrimerDark.class;
            } else {
                return PrimerLight.class;
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return PrimerLight.class;
        }
    }

    private Class<? extends Theme> fromWindowsTheme() {
        try {
            Process process = Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\" /v AppsUseLightTheme");
            process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());
            if (output.contains("REG_DWORD") && output.contains("0x0")) {
                return PrimerDark.class;
            } else {
                return PrimerLight.class;
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return PrimerLight.class;
        }
    }

    private Class<? extends Theme> fromLinuxTheme() {
        String desktopEnv = detectDesktopEnvironment();
        boolean isDark = false;
        if (desktopEnv != null) {
            isDark = switch (desktopEnv) {
                case "KDE Plasma" -> detectKDETheme();
                case "GNOME" -> detectGnomeTheme();
                case "Xfce" -> detectXfceTheme();
                default -> false;
            };
        }
        return isDark ? PrimerDark.class : PrimerLight.class;
    }

    private static String detectDesktopEnvironment() {
        String desktopEnv = System.getenv("XDG_CURRENT_DESKTOP");
        return switch (desktopEnv) {
            case "KDE" -> "KDE Plasma";
            case "GNOME" -> "GNOME";
            case "XFCE" -> "Xfce";
            case null, default -> null;
        };
    }

    /*
    *===============================================================================================
    * THE FOLLOWING METHODS DETECT THE COLORSCHEME CONSIDERING THE CURRENT LINUX DESKTOP ENVIRONMENT
    *===============================================================================================
    */

    /**
     * @return true if a dark mode could be detected, else false
     */
    private static boolean detectKDETheme() {
        // For starters we try using gtk3 settings only
        return detectGnomeTheme();
    }

    /**
     * @return true if a dark mode could be detected, else false
     */
    private static boolean detectXfceTheme() {
        return detectGnomeTheme();
    }

    /**
     * @return true if a dark mode could be detected, else false
     */
    private static boolean detectGnomeTheme() {
        try {
            Process process = Runtime.getRuntime().exec("gsettings get org.gnome.desktop.interface color-scheme");
            process.waitFor();
            String theme = new String(process.getInputStream().readAllBytes()).trim();
            return theme.contains("dark");
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

}