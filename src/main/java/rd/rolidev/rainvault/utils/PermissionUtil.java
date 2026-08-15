package rd.rolidev.rainvault.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionUtil {
    
    public static final String ADMIN = "rainvault.admin";
    public static final String PAY = "rainvault.pay";
    public static final String RELOAD = "rainvault.reload";
    public static final String BYPASS_MAX = "rainvault.bypass.max";
    public static final String BYPASS_TOGGLE = "rainvault.bypass.toggle";

    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public static boolean isAdmin(CommandSender sender) {
        return hasPermission(sender, ADMIN);
    }

    public static boolean canPay(Player player) {
        return hasPermission(player, PAY);
    }

    public static boolean canReload(CommandSender sender) {
        return hasPermission(sender, RELOAD) || isAdmin(sender);
    }

    public static boolean canBypassMax(Player player) {
        return hasPermission(player, BYPASS_MAX);
    }

    public static boolean canBypassToggle(Player player) {
        return hasPermission(player, BYPASS_TOGGLE);
    }
}
