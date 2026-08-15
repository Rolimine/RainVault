package rd.rolidev.rainvault.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import rd.rolidev.rainvault.RainVault;
import rd.rolidev.rainvault.utils.PermissionUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BalanceTopCommand implements CommandExecutor {
    private final RainVault plugin;

    public BalanceTopCommand(RainVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !PermissionUtil.hasPermission(sender, "rainvault.baltop")) {
            plugin.getMessageUtil().sendMessage(sender, "no-permission");
            return true;
        }

        FileConfiguration baltopConfig = plugin.getBaltopConfig();
        String loading = translate(baltopConfig.getString("loading", "&7Loading..."));
        if (!loading.isEmpty()) {
            sender.sendMessage(loading);
        }

        int topSize = plugin.getTopConfig().getInt("top-size", 10);
        plugin.getDatabaseManager().getTopBalances(topSize).thenAccept(topList -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (topList.isEmpty()) {
                    String empty = translate(baltopConfig.getString("empty", "&cTop is empty."));
                    sender.sendMessage(empty);
                    return;
                }

                sendLines(sender, baltopConfig.getStringList("header"));

                String lineFormat = baltopConfig.getString(
                    "line-format",
                    "&e%position%. &f%player% &7- &a%formatted_balance% coins"
                );

                int position = 1;
                for (Map.Entry<UUID, Double> entry : topList) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                    String playerName = player.getName() != null ? player.getName() : "Unknown";
                    String formattedBalance = plugin.getEconomyManager().formatBalance(entry.getValue());

                    String line = lineFormat
                        .replace("%position%", String.valueOf(position))
                        .replace("%player%", playerName)
                        .replace("%balance%", String.valueOf(entry.getValue()))
                        .replace("%formatted_balance%", formattedBalance);

                    sender.sendMessage(translate(line));
                    position++;
                }

                sendLines(sender, baltopConfig.getStringList("footer"));
            });
        }).exceptionally(error -> {
            Bukkit.getScheduler().runTask(plugin, () ->
                sender.sendMessage(ChatColor.RED + "Failed to load top: " + error.getMessage())
            );
            return null;
        });

        return true;
    }

    private void sendLines(CommandSender sender, List<String> lines) {
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            sender.sendMessage(translate(line));
        }
    }

    private String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message == null ? "" : message);
    }
}
