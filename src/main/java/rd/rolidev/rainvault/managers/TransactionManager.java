package rd.rolidev.rainvault.managers;

import org.bukkit.OfflinePlayer;
import rd.rolidev.rainvault.RainVault;
import rd.rolidev.rainvault.models.Transaction;
import rd.rolidev.rainvault.models.TransactionResult;
import rd.rolidev.rainvault.utils.ValidationUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TransactionManager {
    private final RainVault plugin;
    private final ValidationUtil validationUtil;

    public TransactionManager(RainVault plugin) {
        this.plugin = plugin;
        this.validationUtil = new ValidationUtil(plugin);
    }

    public CompletableFuture<TransactionResult> executeTransaction(Transaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            ValidationUtil.ValidationResult validation = validationUtil.validateTransaction(
                transaction.getSender(),
                transaction.getReceiver(),
                transaction.getAmount()
            );

            if (validation != ValidationUtil.ValidationResult.SUCCESS) {
                return new TransactionResult(false, validation.name(), transaction);
            }

            try {
                UUID senderUUID = transaction.getSender().getUniqueId();
                UUID receiverUUID = transaction.getReceiver().getUniqueId();
                double amount = transaction.getAmount();

                double senderBalance = plugin.getDatabaseManager().getBalance(senderUUID).get();
                double receiverBalance = plugin.getDatabaseManager().getBalance(receiverUUID).get();

                plugin.getDatabaseManager().setBalance(senderUUID, senderBalance - amount);
                plugin.getDatabaseManager().setBalance(receiverUUID, receiverBalance + amount);

                return new TransactionResult(true, "SUCCESS", transaction);
            } catch (Exception e) {
                e.printStackTrace();
                return new TransactionResult(false, "DATABASE_ERROR", transaction);
            }
        });
    }

    public CompletableFuture<Boolean> deposit(OfflinePlayer player, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            if (!validationUtil.isValidAmount(amount)) {
                return false;
            }

            if (validationUtil.wouldExceedMaxBalance(player, amount)) {
                return false;
            }

            try {
                UUID uuid = player.getUniqueId();
                double balance = plugin.getDatabaseManager().getBalance(uuid).get();
                plugin.getDatabaseManager().setBalance(uuid, balance + amount);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> withdraw(OfflinePlayer player, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            if (!validationUtil.isValidAmount(amount)) {
                return false;
            }

            if (!validationUtil.canAfford(player, amount)) {
                return false;
            }

            try {
                UUID uuid = player.getUniqueId();
                double balance = plugin.getDatabaseManager().getBalance(uuid).get();
                plugin.getDatabaseManager().setBalance(uuid, balance - amount);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> setBalance(OfflinePlayer player, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            if (amount < 0 || !validationUtil.isWithinMaxBalance(amount)) {
                return false;
            }

            try {
                plugin.getDatabaseManager().setBalance(player.getUniqueId(), amount);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> resetBalance(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                double startBalance = plugin.getConfig().getDouble("starting-balance");
                plugin.getDatabaseManager().setBalance(player.getUniqueId(), startBalance);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}
