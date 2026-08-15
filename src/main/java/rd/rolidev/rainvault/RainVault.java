package rd.rolidev.rainvault;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import rd.rolidev.rainvault.commands.PayCommand;
import rd.rolidev.rainvault.commands.RainVaultCommand;
import rd.rolidev.rainvault.commands.TabCompleter;
import rd.rolidev.rainvault.commands.BalanceTopCommand;
import rd.rolidev.rainvault.database.DatabaseManager;
import rd.rolidev.rainvault.economy.EconomyManager;
import rd.rolidev.rainvault.economy.RainVaultEconomy;
import rd.rolidev.rainvault.listeners.PlayerJoinListener;
import rd.rolidev.rainvault.listeners.PlayerQuitListener;
import rd.rolidev.rainvault.managers.ConfigManager;
import rd.rolidev.rainvault.managers.StatisticsManager;
import rd.rolidev.rainvault.managers.TopManager;
import rd.rolidev.rainvault.managers.TransactionManager;
import rd.rolidev.rainvault.placeholder.RainVaultPlaceholder;
import rd.rolidev.rainvault.utils.LoggerUtil;
import rd.rolidev.rainvault.utils.MessageUtil;
import rd.rolidev.rainvault.utils.ValidationUtil;

public final class RainVault extends JavaPlugin {
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    private RainVaultEconomy economy;
    private ConfigManager configManager;
    private TransactionManager transactionManager;
    private TopManager topManager;
    private StatisticsManager statisticsManager;
    private MessageUtil messageUtil;
    private ValidationUtil validationUtil;
    private LoggerUtil loggerUtil;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        loggerUtil = new LoggerUtil(this);
        messageUtil = new MessageUtil(this);
        validationUtil = new ValidationUtil(this);

        economyManager = new EconomyManager(this);
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        transactionManager = new TransactionManager(this);
        topManager = new TopManager(this);
        statisticsManager = new StatisticsManager(this);

        economy = new RainVaultEconomy(this);
        
        if (!setupEconomy()) {
            getLogger().severe("Vault не найден! Плагин отключается.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands();
        registerListeners();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                new RainVaultPlaceholder(this).register();
                getLogger().info("PlaceholderAPI найден! Плейсхолдеры зарегистрированы.");
            } catch (Exception e) {
                getLogger().warning("Не удалось зарегистрировать PlaceholderAPI: " + e.getMessage());
            }
        }

        getLogger().info("RainVault успешно загружен!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("RainVault отключен!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.Highest);
        getLogger().info("Экономика зарегистрирована в Vault!");
        return true;
    }

    private void registerCommands() {
        TabCompleter tabCompleter = new TabCompleter(this);
        
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("pay").setTabCompleter(tabCompleter);
        
        getCommand("rainvault").setExecutor(new RainVaultCommand(this));
        getCommand("rainvault").setTabCompleter(tabCompleter);

        getCommand("baltop").setExecutor(new BalanceTopCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public FileConfiguration getTopConfig() {
        return configManager.getTopConfig();
    }

    public FileConfiguration getBaltopConfig() {
        return configManager.getBaltopConfig();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public TopManager getTopManager() {
        return topManager;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public ValidationUtil getValidationUtil() {
        return validationUtil;
    }

    public LoggerUtil getLoggerUtil() {
        return loggerUtil;
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }
}
