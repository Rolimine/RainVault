package rd.rolidev.rainvault.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rd.rolidev.rainvault.RainVault;
import rd.rolidev.rainvault.managers.StatisticsManager;
import rd.rolidev.rainvault.utils.NumberFormatter;

public class StatsCommand implements CommandExecutor {
    private final RainVault plugin;

    public StatsCommand(RainVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Укажите имя игрока!");
                return true;
            }
            showPlayerStats(sender, (Player) sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("global")) {
            if (!sender.hasPermission("rainvault.stats.global")) {
                sender.sendMessage(ChatColor.RED + "У вас нет прав!");
                return true;
            }
            showGlobalStats(sender);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден!");
            return true;
        }

        showPlayerStats(sender, target);
        return true;
    }

    private void showPlayerStats(CommandSender sender, OfflinePlayer player) {
        StatisticsManager.PlayerStatistics stats = plugin.getStatisticsManager()
                .getPlayerStatistics(player.getUniqueId());

        sender.sendMessage(ChatColor.GOLD + "=== Статистика " + player.getName() + " ===");
        sender.sendMessage(ChatColor.YELLOW + "Отправлено транзакций: " + 
                ChatColor.WHITE + stats.getTransactionsSent());
        sender.sendMessage(ChatColor.YELLOW + "Получено транзакций: " + 
                ChatColor.WHITE + stats.getTransactionsReceived());
        sender.sendMessage(ChatColor.YELLOW + "Отправлено монет: " + 
                ChatColor.WHITE + NumberFormatter.format(stats.getMoneySent()));
        sender.sendMessage(ChatColor.YELLOW + "Получено монет: " + 
                ChatColor.WHITE + NumberFormatter.format(stats.getMoneyReceived()));
    }

    private void showGlobalStats(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Глобальная статистика ===");
        sender.sendMessage(ChatColor.YELLOW + "Всего транзакций: " + 
                ChatColor.WHITE + plugin.getStatisticsManager().getTotalTransactions());
        sender.sendMessage(ChatColor.YELLOW + "Всего переведено: " + 
                ChatColor.WHITE + NumberFormatter.format(plugin.getStatisticsManager().getTotalMoneyTransferred()));
    }
}
