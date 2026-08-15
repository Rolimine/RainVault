package rd.rolidev.rainvault.economy;

import rd.rolidev.rainvault.RainVault;
import rd.rolidev.rainvault.utils.NumberFormatter;

import java.util.UUID;

public class EconomyManager {
    private final RainVault plugin;

    public EconomyManager(RainVault plugin) {
        this.plugin = plugin;
    }

    public String formatBalance(double balance) {
        return NumberFormatter.format(balance);
    }

    public boolean canAfford(UUID uuid, double amount) {
        try {
            double balance = plugin.getDatabaseManager().getBalance(uuid).get();
            return balance >= amount;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidAmount(double amount) {
        return amount > 0 && amount <= plugin.getConfig().getDouble("max-balance");
    }

    public boolean wouldExceedMax(UUID uuid, double amount) {
        try {
            double balance = plugin.getDatabaseManager().getBalance(uuid).get();
            double maxBalance = plugin.getConfig().getDouble("max-balance");
            return balance + amount > maxBalance;
        } catch (Exception e) {
            return true;
        }
    }

    public double parseAmount(String input) throws NumberFormatException {
        return NumberFormatter.parseAmount(input);
    }
}
