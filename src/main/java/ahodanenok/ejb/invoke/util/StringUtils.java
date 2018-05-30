package ahodanenok.ejb.invoke.util;

public final class StringUtils {

    private StringUtils() { }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
