package rd.rolidev.rainvault.managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import rd.rolidev.rainvault.RainVault;
import rd.rolidev.rainvault.models.TopEntry;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TopManager {
    private final RainVault plugin;
    private List<TopEntry> cachedTop;
    private long lastUpdate;
    private BukkitTask updateTask;

    public TopManager(RainVault plugin) {
        this.plugin = plugin;
        this.cachedTop = new ArrayList<>();
        this.lastUpdate = 0;
        startUpdateTask();
    }

    private void startUpdateTask() {
        int interval = plugin.getTopConfig().getInt("update-interval", 300) * 20;
        
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateTop();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, interval);
    }

    public void reload() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        startUpdateTask();
        updateTop();
    }

    public void updateTop() {
        int topSize = plugin.getTopConfig().getInt("top-size", 10);
        
        plugin.getDatabaseManager().getTopBalances(topSize).thenAccept(topList -> {
            List<TopEntry> newTop = new ArrayList<>();
            int position = 1;
            
            for (Map.Entry<UUID, Double> entry : topList) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                String playerName = player.getName() != null ? player.getName() : "Unknown";
                newTop.add(new TopEntry(position, entry.getKey(), playerName, entry.getValue()));
                position++;
            }
            
            cachedTop = newTop;
            lastUpdate = System.currentTimeMillis();
        });
    }

    public CompletableFuture<Optional<TopEntry>> getTopEntry(int position) {
        return CompletableFuture.supplyAsync(() -> {
            if (position < 1 || position > cachedTop.size()) {
                return Optional.empty();
            }
            return Optional.of(cachedTop.get(position - 1));
        });
    }

    public List<TopEntry> getCachedTop() {
        return new ArrayList<>(cachedTop);
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public CompletableFuture<Integer> getPlayerPosition(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < cachedTop.size(); i++) {
                if (cachedTop.get(i).getUuid().equals(uuid)) {
                    return i + 1;
                }
            }
            return -1;
        });
    }
}
