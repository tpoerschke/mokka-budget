package de.timkodiert.mokka.properties;

public enum OperatingSystem{
    WINDOWS("Windows"),
    OSX("Mac OS X"),
    LINUX("Linux"),
    UNKNOWN("Unknown");

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final String displayName;

    OperatingSystem(String osName) {
        this.displayName = osName;
    }

    public static OperatingSystem get(){
        String osName = System.getProperty("os.name");
        return fromString(osName);
    }

    private static OperatingSystem fromString(String osName) {
        if (osName.startsWith("Windows")) {
            return WINDOWS;
        } else if (osName.startsWith("Mac OS X")) {
            return OSX;
        } else if (osName.startsWith("Linux")) {
            return LINUX;
        } else {
            return UNKNOWN;
        }
    }
}