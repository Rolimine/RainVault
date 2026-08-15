package rd.rolidev.rainvault.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberFormatter {
    private static final DecimalFormat DECIMAL_FORMAT;
    
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        DECIMAL_FORMAT = new DecimalFormat("#,###", symbols);
    }

    public static String format(double amount) {
        if (amount >= 1_000_000_000) {
            return formatBillions(amount);
        } else if (amount >= 100_000_000) {
            return formatMillions(amount);
        } else if (amount >= 1_000) {
            return formatWithSpaces((long) amount);
        }
        return String.format("%.0f", amount);
    }

    private static String formatBillions(double amount) {
        double billions = amount / 1_000_000_000;
        if (billions >= 100) {
            return String.format("%.0fmm", billions);
        } else if (billions >= 10) {
            return String.format("%.1fmm", billions);
        } else {
            return String.format("%.2fmm", billions);
        }
    }

    private static String formatMillions(double amount) {
        double millions = amount / 1_000_000;
        if (millions >= 100) {
            return String.format("%.0fm", millions);
        } else if (millions >= 10) {
            return String.format("%.1fm", millions);
        } else {
            return String.format("%.2fm", millions);
        }
    }

    private static String formatWithSpaces(long number) {
        return DECIMAL_FORMAT.format(number);
    }

    public static boolean isValidNumber(String input) {
        try {
            double value = Double.parseDouble(input);
            return !Double.isNaN(value) && !Double.isInfinite(value) && value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double parseAmount(String input) throws NumberFormatException {
        String cleaned = input.toLowerCase().trim();
        
        if (cleaned.endsWith("mm")) {
            return Double.parseDouble(cleaned.substring(0, cleaned.length() - 2)) * 1_000_000_000;
        } else if (cleaned.endsWith("m")) {
            return Double.parseDouble(cleaned.substring(0, cleaned.length() - 1)) * 1_000_000;
        } else if (cleaned.endsWith("k")) {
            return Double.parseDouble(cleaned.substring(0, cleaned.length() - 1)) * 1_000;
        }
        
        return Double.parseDouble(cleaned);
    }
}
