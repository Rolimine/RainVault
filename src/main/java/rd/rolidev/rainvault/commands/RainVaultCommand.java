package rd.rolidev.rainvault.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rd.rolidev.rainvault.RainVault;
import rd.rolidev.rainvault.utils.PermissionUtil;

public class RainVaultCommand implements CommandExecutor {
    private final RainVault plugin;

    public RainVaultCommand(RainVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, "usage-rainvault");
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("reload")) {
            handleReload(sender);
            return true;
        }

        if (!sender.hasPermission("rainvault.admin")) {
            sendMessage(sender, "no-permission");
            return true;
        }

        if (args.length < 2) {
            sendMessage(sender, "usage-rainvault");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        switch (action) {
            case "give":
                if (args.length < 3) {
                    sendMessage(sender, "usage-rainvault");
                    return true;
                }
                handleGive(sender, target, args[2]);
                break;

            case "set":
                if (args.length < 3) {
                    sendMessage(sender, "usage-rainvault");
                    return true;
                }
                handleSet(sender, target, args[2]);
                break;

            case "take":
                if (args.length < 3) {
                    sendMessage(sender, "usage-rainvault");
                    return true;
                }
                handleTake(sender, target, args[2]);
                break;

            case "check":
                handleCheck(sender, target);
                break;

            case "reset":
                handleReset(sender, target);
                break;

            default:
                sendMessage(sender, "usage-rainvault");
                break;
        }

        return true;
    }

    private void handleGive(CommandSender sender, OfflinePlayer target, String amountStr) {
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            sendMessage(sender, "invalid-amount");
            return;
        }

        if (!plugin.getEconomyManager().isValidAmount(amount)) {
            sendMessage(sender, "invalid-amount");
            return;
        }

        plugin.getDatabaseManager().getBalance(target.getUniqueId()).thenAccept(balance -> {
            double maxBalance = plugin.getConfig().getDouble("max-balance");
            if (balance + amount > maxBalance) {
                sendMessage(sender, "max-balance-reached");
                return;
            }

            plugin.getDatabaseManager().setBalance(target.getUniqueId(), balance + amount);
            String formatted = plugin.getEconomyManager().formatBalance(amount);
            sendMessage(sender, "admin-give", target.getName(), formatted);

            if (target.isOnline()) {
                Player onlineTarget = target.getPlayer();
                sendMessage(onlineTarget, "admin-give-received", "", formatted);
            }
        });
    }

    private void handleSet(CommandSender sender, OfflinePlayer target, String amountStr) {
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            sendMessage(sender, "invalid-amount");
            return;
        }

        if (amount < 0 || amount > plugin.getConfig().getDouble("max-balance")) {
            sendMessage(sender, "invalid-amount");
            return;
        }

        plugin.getDatabaseManager().setBalance(target.getUniqueId(), amount);
        String formatted = plugin.getEconomyManager().formatBalance(amount);
        sendMessage(sender, "admin-set", target.getName(), formatted);

        if (target.isOnline()) {
            Player onlineTarget = target.getPlayer();
            sendMessage(onlineTarget, "admin-set-received", "", formatted);
        }
    }

    private void handleTake(CommandSender sender, OfflinePlayer target, String amountStr) {
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            sendMessage(sender, "invalid-amount");
            return;
        }

        if (!plugin.getEconomyManager().isValidAmount(amount)) {
            sendMessage(sender, "invalid-amount");
            return;
        }

        plugin.getDatabaseManager().getBalance(target.getUniqueId()).thenAccept(balance -> {
            if (balance < amount) {
                sendMessage(sender, "insufficient-balance");
                return;
            }

            plugin.getDatabaseManager().setBalance(target.getUniqueId(), balance - amount);
            String formatted = plugin.getEconomyManager().formatBalance(amount);
            sendMessage(sender, "admin-take", target.getName(), formatted);

            if (target.isOnline()) {
                Player onlineTarget = target.getPlayer();
                sendMessage(onlineTarget, "admin-take-received", "", formatted);
            }
        });
    }

    private void handleCheck(CommandSender sender, OfflinePlayer target) {
        plugin.getDatabaseManager().getBalance(target.getUniqueId()).thenAccept(balance -> {
            String formatted = plugin.getEconomyManager().formatBalance(balance);
            sendMessage(sender, "admin-check", target.getName(), formatted);
        });
    }

    private void handleReset(CommandSender sender, OfflinePlayer target) {
        double startBalance = plugin.getConfig().getDouble("starting-balance");
        plugin.getDatabaseManager().setBalance(target.getUniqueId(), startBalance);
        String formatted = plugin.getEconomyManager().formatBalance(startBalance);
        sendMessage(sender, "admin-reset", target.getName(), formatted);

        if (target.isOnline()) {
            Player onlineTarget = target.getPlayer();
            sendMessage(onlineTarget, "admin-reset-received", "", formatted);
        }
    }

    private void handleReload(CommandSender sender) {
        if (!PermissionUtil.canReload(sender)) {
            sendMessage(sender, "no-permission");
            return;
        }

        try {
            plugin.reloadConfig();
            plugin.getConfigManager().reloadConfigs();
            plugin.getTopManager().reload();
            sendMessage(sender, "reload-success");
        } catch (Exception e) {
            sendMessage(sender, "reload-fail");
            plugin.getLogger().warning("Reload failed: " + e.getMessage());
        }
    }

    private void sendMessage(CommandSender sender, String key) {
        sendMessage(sender, key, "", "");
    }

    private void sendMessage(CommandSender sender, String key, String playerName, String amount) {
        String prefix = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("messages.prefix"));
        String message = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("messages." + key));
        
        message = message.replace("%player%", playerName).replace("%amount%", amount);
        sender.sendMessage(prefix + message);
    }
}
