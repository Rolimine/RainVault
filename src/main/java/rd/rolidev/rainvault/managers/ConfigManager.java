package rd.rolidev.rainvault.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import rd.rolidev.rainvault.RainVault;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final RainVault plugin;
    private FileConfiguration config;
    private FileConfiguration topConfig;
    private FileConfiguration baltopConfig;
    private File configFile;
    private File topConfigFile;
    private File baltopConfigFile;

    public ConfigManager(RainVault plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    private void loadConfigs() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");

        topConfigFile = new File(plugin.getDataFolder(), "rvaulttop.yml");
        ensureResource(topConfigFile, "rvaulttop.yml");
        topConfig = YamlConfiguration.loadConfiguration(topConfigFile);

        baltopConfigFile = new File(plugin.getDataFolder(), "baltop.yml");
        ensureResource(baltopConfigFile, "baltop.yml");
        baltopConfig = YamlConfiguration.loadConfiguration(baltopConfigFile);
    }

    public void reloadConfigs() {
        config = YamlConfiguration.loadConfiguration(configFile);
        ensureResource(topConfigFile, "rvaulttop.yml");
        topConfig = YamlConfiguration.loadConfiguration(topConfigFile);
        ensureResource(baltopConfigFile, "baltop.yml");
        baltopConfig = YamlConfiguration.loadConfiguration(baltopConfigFile);
    }

    private void ensureResource(File file, String resourceName) {
        if (!file.exists()) {
            plugin.saveResource(resourceName, false);
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить config.yml: " + e.getMessage());
        }
    }

    public void saveTopConfig() {
        try {
            topConfig.save(topConfigFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить rvaulttop.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getTopConfig() {
        return topConfig;
    }

    public FileConfiguration getBaltopConfig() {
        return baltopConfig;
    }

    public double getStartingBalance() {
        return config.getDouble("starting-balance", 1000);
    }

    public double getMaxBalance() {
        return config.getDouble("max-balance", 100_000_000_000L);
    }

    public boolean isMySQLEnabled() {
        return config.getBoolean("mysql.enabled", true);
    }

    public String getMySQLHost() {
        return config.getString("mysql.host", "localhost");
    }

    public int getMySQLPort() {
        return config.getInt("mysql.port", 3306);
    }

    public String getMySQLDatabase() {
        return config.getString("mysql.database", "minecraft");
    }

    public String getMySQLUsername() {
        return config.getString("mysql.username", "root");
    }

    public String getMySQLPassword() {
        return config.getString("mysql.password", "password");
    }

    public String getMySQLTable() {
        return config.getString("mysql.table", "RainVault");
    }

    public int getTopSize() {
        return topConfig.getInt("top-size", 10);
    }

    public int getTopUpdateInterval() {
        return topConfig.getInt("update-interval", 300);
    }

    public String getTopFormat() {
        return topConfig.getString("top-format", "&e%position%. &f%player% &7- &a%formatted_balance% монет");
    }
}
