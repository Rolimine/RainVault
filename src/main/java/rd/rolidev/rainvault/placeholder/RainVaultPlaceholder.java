package rd.rolidev.rainvault.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import rd.rolidev.rainvault.RainVault;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RainVaultPlaceholder extends PlaceholderExpansion {
    private final RainVault plugin;

    public RainVaultPlaceholder(RainVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rainvault";
    }

    @Override
    public @NotNull String getAuthor() {
        return "RoliDev";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        // %rainvault_balance%
        if (params.equalsIgnoreCase("balance")) {
            if (player == null) return "0";
            try {
                double balance = plugin.getDatabaseManager().getBalance(player.getUniqueId()).get();
                return plugin.getEconomyManager().formatBalance(balance);
            } catch (Exception e) {
                return "0";
            }
        }

        // %rainvault_top_N%
        if (params.startsWith("top_")) {
            try {
                int position = Integer.parseInt(params.substring(4));
                return getTopPlayer(position);
            } catch (NumberFormatException e) {
                return "Неверный формат";
            }
        }

        return null;
    }

    private String getTopPlayer(int position) {
        try {
            List<Map.Entry<UUID, Double>> topList = plugin.getDatabaseManager()
                .getTopBalances(plugin.getTopConfig().getInt("top-size")).get();

            if (position < 1 || position > topList.size()) {
                return "N/A";
            }

            Map.Entry<UUID, Double> entry = topList.get(position - 1);
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            String formattedBalance = plugin.getEconomyManager().formatBalance(entry.getValue());

            String format = plugin.getTopConfig().getString("top-format");
            format = format.replace("%position%", String.valueOf(position));
            format = format.replace("%player%", player.getName() != null ? player.getName() : "Unknown");
            format = format.replace("%balance%", String.valueOf(entry.getValue()));
            format = format.replace("%formatted_balance%", formattedBalance);

            return org.bukkit.ChatColor.translateAlternateColorCodes('&', format);
        } catch (Exception e) {
            return "Ошибка";
        }
    }
}
