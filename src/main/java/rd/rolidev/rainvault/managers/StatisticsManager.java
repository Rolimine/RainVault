package rd.rolidev.rainvault.managers;

import rd.rolidev.rainvault.RainVault;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticsManager {
    private final RainVault plugin;
    private final ConcurrentHashMap<UUID, PlayerStatistics> playerStats;
    private final AtomicLong totalTransactions;
    private final AtomicLong totalMoneyTransferred;

    public StatisticsManager(RainVault plugin) {
        this.plugin = plugin;
        this.playerStats = new ConcurrentHashMap<>();
        this.totalTransactions = new AtomicLong(0);
        this.totalMoneyTransferred = new AtomicLong(0);
    }

    public void recordTransaction(UUID sender, UUID receiver, double amount) {
        totalTransactions.incrementAndGet();
        totalMoneyTransferred.addAndGet((long) amount);

        playerStats.computeIfAbsent(sender, k -> new PlayerStatistics())
                .recordSent(amount);
        playerStats.computeIfAbsent(receiver, k -> new PlayerStatistics())
                .recordReceived(amount);
    }

    public PlayerStatistics getPlayerStatistics(UUID uuid) {
        return playerStats.getOrDefault(uuid, new PlayerStatistics());
    }

    public long getTotalTransactions() {
        return totalTransactions.get();
    }

    public long getTotalMoneyTransferred() {
        return totalMoneyTransferred.get();
    }

    public void resetPlayerStatistics(UUID uuid) {
        playerStats.remove(uuid);
    }

    public void resetAllStatistics() {
        playerStats.clear();
        totalTransactions.set(0);
        totalMoneyTransferred.set(0);
    }

    public static class PlayerStatistics {
        private final AtomicLong transactionsSent;
        private final AtomicLong transactionsReceived;
        private final AtomicLong moneySent;
        private final AtomicLong moneyReceived;

        public PlayerStatistics() {
            this.transactionsSent = new AtomicLong(0);
            this.transactionsReceived = new AtomicLong(0);
            this.moneySent = new AtomicLong(0);
            this.moneyReceived = new AtomicLong(0);
        }

        public void recordSent(double amount) {
            transactionsSent.incrementAndGet();
            moneySent.addAndGet((long) amount);
        }

        public void recordReceived(double amount) {
            transactionsReceived.incrementAndGet();
            moneyReceived.addAndGet((long) amount);
        }

        public long getTransactionsSent() {
            return transactionsSent.get();
        }

        public long getTransactionsReceived() {
            return transactionsReceived.get();
        }

        public long getMoneySent() {
            return moneySent.get();
        }

        public long getMoneyReceived() {
            return moneyReceived.get();
        }

        public long getTotalTransactions() {
            return transactionsSent.get() + transactionsReceived.get();
        }
    }
}
