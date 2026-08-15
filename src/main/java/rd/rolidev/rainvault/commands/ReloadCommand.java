package rd.rolidev.rainvault.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import rd.rolidev.rainvault.RainVault;
import rd.rolidev.rainvault.utils.PermissionUtil;

public class ReloadCommand implements CommandExecutor {
    private final RainVault plugin;

    public ReloadCommand(RainVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionUtil.canReload(sender)) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав на использование этой команды!");
            return true;
        }

        try {
            plugin.reloadConfig();
            plugin.getConfigManager().reloadConfigs();
            sender.sendMessage(ChatColor.GREEN + "Конфигурация RainVault перезагружена!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Ошибка при перезагрузке конфигурации: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
