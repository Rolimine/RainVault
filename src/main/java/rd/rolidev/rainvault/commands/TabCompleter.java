package rd.rolidev.rainvault.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rd.rolidev.rainvault.RainVault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    private final RainVault plugin;

    public TabCompleter(RainVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("rainvault")) {
            return handleRainVaultTab(sender, args);
        } else if (command.getName().equalsIgnoreCase("pay")) {
            return handlePayTab(sender, args);
        }
        return new ArrayList<>();
    }

    private List<String> handleRainVaultTab(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rainvault.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return filterStartingWith(Arrays.asList("give", "set", "take", "check", "reset", "reload"), args[0]);
        }

        if (args.length == 2) {
            return filterStartingWith(getOnlinePlayerNames(), args[1]);
        }

        if (args.length == 3 && !args[0].equalsIgnoreCase("check") && !args[0].equalsIgnoreCase("reset")) {
            return Arrays.asList("100", "1000", "10000", "100000");
        }

        return new ArrayList<>();
    }

    private List<String> handlePayTab(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>(getOnlinePlayerNames());
            suggestions.add("toggle");
            return filterStartingWith(suggestions, args[0]);
        }

        if (args.length == 2 && !args[0].equalsIgnoreCase("toggle")) {
            return Arrays.asList("100", "1000", "10000");
        }

        return new ArrayList<>();
    }

    private List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .collect(Collectors.toList());
    }

    private List<String> filterStartingWith(List<String> list, String prefix) {
        return list.stream()
            .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
            .collect(Collectors.toList());
    }
}
