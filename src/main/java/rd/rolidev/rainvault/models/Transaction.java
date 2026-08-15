package rd.rolidev.rainvault.models;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class Transaction {
    private final OfflinePlayer sender;
    private final OfflinePlayer receiver;
    private final double amount;
    private final TransactionType type;
    private final long timestamp;

    public Transaction(OfflinePlayer sender, OfflinePlayer receiver, double amount, TransactionType type) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public OfflinePlayer getSender() {
        return sender;
    }

    public OfflinePlayer getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public UUID getSenderUUID() {
        return sender.getUniqueId();
    }

    public UUID getReceiverUUID() {
        return receiver.getUniqueId();
    }

    public enum TransactionType {
        PLAYER_TO_PLAYER,
        ADMIN_GIVE,
        ADMIN_TAKE,
        ADMIN_SET,
        ADMIN_RESET
    }
}
