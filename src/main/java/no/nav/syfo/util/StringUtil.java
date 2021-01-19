package no.nav.syfo.util;

public class StringUtil {
    public static String lowerCapitalize(String input) {
        if (input == null) {
            return null;
        }
        String lowercaseInput = input.toLowerCase();
        return lowercaseInput.substring(0, 1).toUpperCase() + lowercaseInput.substring(1);
    }
}
