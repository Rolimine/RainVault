package rd.rolidev.rainvault.utils;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String message) {
        if (message == null) {
            return "";
        }

        // Поддержка HEX цветов для 1.16+
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            String replacement = net.md_5.bungee.api.ChatColor.of("#" + hexCode).toString();
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static String stripColor(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.stripColor(colorize(message));
    }

    public static String getBalanceColor(double balance) {
        if (balance >= 1_000_000_000) {
            return "&d"; // Светло-фиолетовый
        } else if (balance >= 100_000_000) {
            return "&6"; // Золотой
        } else if (balance >= 10_000_000) {
            return "&e"; // Желтый
        } else if (balance >= 1_000_000) {
            return "&a"; // Зеленый
        } else if (balance >= 100_000) {
            return "&2"; // Темно-зеленый
        } else if (balance >= 10_000) {
            return "&b"; // Голубой
        } else if (balance >= 1_000) {
            return "&f"; // Белый
        } else {
            return "&7"; // Серый
        }
    }
}
