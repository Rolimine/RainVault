package rd.rolidev.rainvault.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import rd.rolidev.rainvault.RainVault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private final RainVault plugin;
    private HikariDataSource dataSource;
    private final Map<UUID, Double> balanceCache = new HashMap<>();

    public DatabaseManager(RainVault plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + 
            plugin.getConfig().getString("mysql.host") + ":" + 
            plugin.getConfig().getInt("mysql.port") + "/" + 
            plugin.getConfig().getString("mysql.database"));
        config.setUsername(plugin.getConfig().getString("mysql.username"));
        config.setPassword(plugin.getConfig().getString("mysql.password"));
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        
        dataSource = new HikariDataSource(config);
        createTable();
    }

    private void createTable() {
        String table = plugin.getConfig().getString("mysql.table");
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "CREATE TABLE IF NOT EXISTS " + table + " (" +
                 "uuid VARCHAR(36) PRIMARY KEY," +
                 "balance DOUBLE NOT NULL," +
                 "pay_toggle BOOLEAN DEFAULT TRUE)"
             )) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public CompletableFuture<Double> getBalance(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (balanceCache.containsKey(uuid)) {
                return balanceCache.get(uuid);
            }
            
            String table = plugin.getConfig().getString("mysql.table");
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT balance FROM " + table + " WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    balanceCache.put(uuid, balance);
                    return balance;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            double startBalance = plugin.getConfig().getDouble("starting-balance");
            setBalance(uuid, startBalance);
            return startBalance;
        });
    }

    public void setBalance(UUID uuid, double amount) {
        balanceCache.put(uuid, amount);
        CompletableFuture.runAsync(() -> {
            String table = plugin.getConfig().getString("mysql.table");
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + table + " (uuid, balance) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE balance = ?")) {
                stmt.setString(1, uuid.toString());
                stmt.setDouble(2, amount);
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Boolean> getPayToggle(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            String table = plugin.getConfig().getString("mysql.table");
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT pay_toggle FROM " + table + " WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getBoolean("pay_toggle");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    public void setPayToggle(UUID uuid, boolean toggle) {
        CompletableFuture.runAsync(() -> {
            String table = plugin.getConfig().getString("mysql.table");
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + table + " (uuid, balance, pay_toggle) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE pay_toggle = ?")) {
                stmt.setString(1, uuid.toString());
                stmt.setDouble(2, plugin.getConfig().getDouble("starting-balance"));
                stmt.setBoolean(3, toggle);
                stmt.setBoolean(4, toggle);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<List<Map.Entry<UUID, Double>>> getTopBalances(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<Map.Entry<UUID, Double>> topList = new ArrayList<>();
            String table = plugin.getConfig().getString("mysql.table");
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT uuid, balance FROM " + table + " ORDER BY balance DESC")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next() && topList.size() < limit) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    if (!isKnownServerPlayer(uuid)) {
                        continue;
                    }

                    double balance = rs.getDouble("balance");
                    topList.add(new AbstractMap.SimpleEntry<>(uuid, balance));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return topList;
        });
    }

    private boolean isKnownServerPlayer(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.isOnline() || player.hasPlayedBefore() || player.getName() != null;
    }

    public void clearCache(UUID uuid) {
        balanceCache.remove(uuid);
    }
}
