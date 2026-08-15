package rd.rolidev.rainvault.utils;

import rd.rolidev.rainvault.RainVault;
import rd.rolidev.rainvault.models.Transaction;

import java.util.logging.Level;

public class LoggerUtil {
    private final RainVault plugin;

    public LoggerUtil(RainVault plugin) {
        this.plugin = plugin;
    }

    public void logTransaction(Transaction transaction) {
        String message = String.format(
            "[Transaction] Type: %s, Sender: %s, Receiver: %s, Amount: %.2f",
            transaction.getType(),
            transaction.getSender().getName(),
            transaction.getReceiver().getName(),
            transaction.getAmount()
        );
        plugin.getLogger().info(message);
    }

    public void logError(String message, Exception e) {
        plugin.getLogger().log(Level.SEVERE, message, e);
    }

    public void logWarning(String message) {
        plugin.getLogger().warning(message);
    }

    public void logInfo(String message) {
        plugin.getLogger().info(message);
    }

    public void logDebug(String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }
}
