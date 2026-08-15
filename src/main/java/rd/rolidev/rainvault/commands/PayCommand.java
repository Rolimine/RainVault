package rd.rolidev.rainvault.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rd.rolidev.rainvault.RainVault;

public class PayCommand implements CommandExecutor {
    private final RainVault plugin;

    public PayCommand(RainVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("rainvault.pay")) {
            sendMessage(player, "no-permission");
            return true;
        }

        if (args.length == 0) {
            sendMessage(player, "usage-pay");
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            plugin.getDatabaseManager().getPayToggle(player.getUniqueId()).thenAccept(currentToggle -> {
                boolean newToggle = !currentToggle;
                plugin.getDatabaseManager().setPayToggle(player.getUniqueId(), newToggle);
                
                if (newToggle) {
                    sendMessage(player, "pay-toggle-enabled");
                } else {
                    sendMessage(player, "pay-toggle-disabled");
                }
            });
            return true;
        }

        if (args.length < 2) {
            sendMessage(player, "usage-pay");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sendMessage(player, "player-not-found");
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            sendMessage(player, "pay-self");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sendMessage(player, "invalid-amount");
            return true;
        }

        if (!plugin.getEconomyManager().isValidAmount(amount)) {
            sendMessage(player, "invalid-amount");
            return true;
        }

        plugin.getDatabaseManager().getPayToggle(target.getUniqueId()).thenAccept(canReceive -> {
            if (!canReceive) {
                sendMessage(player, "pay-disabled-target", target.getName(), String.valueOf(amount));
                return;
            }

            plugin.getDatabaseManager().getBalance(player.getUniqueId()).thenAccept(balance -> {
                if (balance < amount) {
                    sendMessage(player, "pay-insufficient", "", String.valueOf(amount));
                    return;
                }

                if (plugin.getEconomyManager().wouldExceedMax(target.getUniqueId(), amount)) {
                    sendMessage(player, "max-balance-reached");
                    return;
                }

                plugin.getDatabaseManager().setBalance(player.getUniqueId(), balance - amount);
                
                plugin.getDatabaseManager().getBalance(target.getUniqueId()).thenAccept(targetBalance -> {
                    plugin.getDatabaseManager().setBalance(target.getUniqueId(), targetBalance + amount);
                    
                    String formattedAmount = plugin.getEconomyManager().formatBalance(amount);
                    sendMessage(player, "pay-sent", target.getName(), formattedAmount);
                    sendMessage(target, "pay-received", player.getName(), formattedAmount);
                });
            });
        });

        return true;
    }

    private void sendMessage(Player player, String key) {
        sendMessage(player, key, "", "");
    }

    private void sendMessage(Player player, String key, String playerName, String amount) {
        String prefix = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("messages.prefix"));
        String message = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("messages." + key));
        
        message = message.replace("%player%", playerName).replace("%amount%", amount);
        player.sendMessage(prefix + message);
    }
}
