package rd.rolidev.rainvault.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import rd.rolidev.rainvault.RainVault;

import java.util.HashMap;
import java.util.Map;

public class MessageUtil {
    private final RainVault plugin;

    public MessageUtil(RainVault plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(CommandSender sender, String key) {
        sendMessage(sender, key, new HashMap<>());
    }

    public void sendMessage(CommandSender sender, String key, Map<String, String> placeholders) {
        FileConfiguration config = plugin.getConfig();
        String prefix = ChatColor.translateAlternateColorCodes('&', 
            config.getString("messages.prefix", "&8[&bRainVault&8] &7"));
        String message = ChatColor.translateAlternateColorCodes('&', 
            config.getString("messages." + key, "&cСообщение не найдено: " + key));

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        sender.sendMessage(prefix + message);
    }

    public void sendMessageWithPlayer(CommandSender sender, String key, String playerName, String amount) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", playerName);
        placeholders.put("amount", amount);
        sendMessage(sender, key, placeholders);
    }

    public String formatMessage(String key, Map<String, String> placeholders) {
        FileConfiguration config = plugin.getConfig();
        String message = ChatColor.translateAlternateColorCodes('&', 
            config.getString("messages." + key, "&cСообщение не найдено: " + key));

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        return message;
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("messages.prefix", "&8[&bRainVault&8] &7"));
    }
}
