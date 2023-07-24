package fr.vivicoubar.thirstplugin;

import fr.vivicoubar.thirstplugin.command.ThirstCommand;
import fr.vivicoubar.thirstplugin.listeners.PlayerListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ThirstPlugin extends JavaPlugin {

    private File statsFile;
    private File configFile;

    private YamlConfiguration statsConfiguration;
    private YamlConfiguration yamlConfiguration;

    private ThirstExpansion thirstExpansion;

    private final HashMap<String, Double> thirsts = new HashMap<>();
    private final HashMap<String, Boolean> allowThirsts = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(this), this);
        Objects.requireNonNull(getCommand("thirst")).setExecutor(new ThirstCommand(this));

        statsFile = new File(getDataFolder(),"stats.yml");
        configFile = new File(getDataFolder(),"config.yml");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        if(!configFile.exists()) {
            getLogger().info("config.yml not found, creating...");
            try {
                configFile.createNewFile();
                yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
                Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(this.getResource("config.yml")), StandardCharsets.UTF_8);
                if (defConfigStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    yamlConfiguration.setDefaults(defConfig);
                }
                yamlConfiguration.options().copyDefaults(true);
                yamlConfiguration.save(configFile);
            } catch (IOException e) {
                this.getPluginLoader().disablePlugin(this);
                throw new RuntimeException(e);
            }
        }
        if(!statsFile.exists()) {
            getLogger().info("stats.yml not found, creating...");
            try {
                statsFile.createNewFile();
                statsConfiguration = YamlConfiguration.loadConfiguration(statsFile);
                Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(this.getResource("stats.yml")), StandardCharsets.UTF_8);
                if (defConfigStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    statsConfiguration.setDefaults(defConfig);
                }
                statsConfiguration.options().copyDefaults(true);
                statsConfiguration.save(statsFile);

            } catch (IOException e) {
                this.getPluginLoader().disablePlugin(this);
                throw new RuntimeException(e);
            }

        }
        statsConfiguration = YamlConfiguration.loadConfiguration(statsFile);
        yamlConfiguration = YamlConfiguration.loadConfiguration(configFile);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ThirstExpansion(this).register();
        }
        getLogger().info("Launched with success!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public YamlConfiguration getStatsConfig() {
        return statsConfiguration;
    }

    public File getStatsFile() {
        return statsFile;
    }

    public YamlConfiguration getYamlConfig() {
        return yamlConfiguration;
    }

    public File getConfigFile() {
        return configFile;
    }

    public HashMap<String, Double> getThirsts() {
        return thirsts;
    }

    public HashMap<String, Boolean> getAllowThirsts() {
        return allowThirsts;
    }
}
