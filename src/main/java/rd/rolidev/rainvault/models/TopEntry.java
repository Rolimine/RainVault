package rd.rolidev.rainvault.models;

import java.util.UUID;

public class TopEntry {
    private final int position;
    private final UUID uuid;
    private final String playerName;
    private final double balance;

    public TopEntry(int position, UUID uuid, String playerName, double balance) {
        this.position = position;
        this.uuid = uuid;
        this.playerName = playerName;
        this.balance = balance;
    }

    public int getPosition() {
        return position;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getBalance() {
        return balance;
    }
}
