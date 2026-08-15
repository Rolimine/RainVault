package rd.rolidev.rainvault.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import rd.rolidev.rainvault.RainVault;

public class PlayerJoinListener implements Listener {
    private final RainVault plugin;

    public PlayerJoinListener(RainVault plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getDatabaseManager().getBalance(event.getPlayer().getUniqueId()).thenAccept(balance -> {
            if (balance == 0) {
                double startBalance = plugin.getConfig().getDouble("starting-balance");
                plugin.getDatabaseManager().setBalance(event.getPlayer().getUniqueId(), startBalance);
            }
        });
    }
}
