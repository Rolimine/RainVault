package rd.rolidev.rainvault.utils;

import org.bukkit.OfflinePlayer;
import rd.rolidev.rainvault.RainVault;

public class ValidationUtil {
    private final RainVault plugin;

    public ValidationUtil(RainVault plugin) {
        this.plugin = plugin;
    }

    public boolean isValidAmount(double amount) {
        return amount > 0 && !Double.isNaN(amount) && !Double.isInfinite(amount);
    }

    public boolean isWithinMaxBalance(double amount) {
        double maxBalance = plugin.getConfig().getDouble("max-balance");
        return amount <= maxBalance;
    }

    public boolean canAfford(OfflinePlayer player, double amount) {
        try {
            double balance = plugin.getDatabaseManager().getBalance(player.getUniqueId()).get();
            return balance >= amount;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean wouldExceedMaxBalance(OfflinePlayer player, double amount) {
        try {
            double balance = plugin.getDatabaseManager().getBalance(player.getUniqueId()).get();
            double maxBalance = plugin.getConfig().getDouble("max-balance");
            return balance + amount > maxBalance;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isPositive(double amount) {
        return amount > 0;
    }

    public boolean isValidPlayer(OfflinePlayer player) {
        return player != null && player.hasPlayedBefore();
    }

    public ValidationResult validateTransaction(OfflinePlayer sender, OfflinePlayer receiver, double amount) {
        if (!isValidPlayer(sender)) {
            return ValidationResult.INVALID_SENDER;
        }

        if (!isValidPlayer(receiver)) {
            return ValidationResult.INVALID_RECEIVER;
        }

        if (sender.getUniqueId().equals(receiver.getUniqueId())) {
            return ValidationResult.SAME_PLAYER;
        }

        if (!isValidAmount(amount)) {
            return ValidationResult.INVALID_AMOUNT;
        }

        if (!isWithinMaxBalance(amount)) {
            return ValidationResult.AMOUNT_TOO_HIGH;
        }

        if (!canAfford(sender, amount)) {
            return ValidationResult.INSUFFICIENT_FUNDS;
        }

        if (wouldExceedMaxBalance(receiver, amount)) {
            return ValidationResult.RECEIVER_MAX_BALANCE;
        }

        return ValidationResult.SUCCESS;
    }

    public enum ValidationResult {
        SUCCESS,
        INVALID_SENDER,
        INVALID_RECEIVER,
        SAME_PLAYER,
        INVALID_AMOUNT,
        AMOUNT_TOO_HIGH,
        INSUFFICIENT_FUNDS,
        RECEIVER_MAX_BALANCE
    }
}
