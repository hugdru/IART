package utils;

public final class Strings {
    public static boolean isBlank(String string) {
        return com.google.common.base.Strings.isNullOrEmpty(string) || string.trim().isEmpty();
    }

    public static boolean notBlank(String string) {
        return !com.google.common.base.Strings.isNullOrEmpty(string) && !string.trim().isEmpty();
    }
}
