package rd.rolidev.rainvault.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import rd.rolidev.rainvault.RainVault;

public class PlayerQuitListener implements Listener {
    private final RainVault plugin;

    public PlayerQuitListener(RainVault plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getDatabaseManager().clearCache(event.getPlayer().getUniqueId());
    }
}
