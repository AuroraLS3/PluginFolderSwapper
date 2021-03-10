package com.djrapitops.pfs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public class PFS extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = getLogger();

        loadDefaultConfig();

        if (getConfig().getBoolean("Check_for_updates")) {
            logger.info(VersionChecker.checkVersion());
        }
        logger.info("Plugin Folder Swapper enabled.");

        logger.info("-- Loading plugins.. --");
        List<String> loadedPlugins = loadPlugins();
        logger.info("-- Plugins loaded. --");

        logPluginInformation(loadedPlugins);
    }

    private void logPluginInformation(List<String> loadedPlugins) {
        StringBuilder loadedBuilder = new StringBuilder("Loaded " + loadedPlugins.size() + " plugins: ");
        PluginManager pluginManager = getServer().getPluginManager();
        for (String loadedPlugin : loadedPlugins) {
            Plugin plugin = pluginManager.getPlugin(loadedPlugin);
            if (plugin == null) {
                loadedBuilder.append(ChatColor.RED).append(loadedPlugin);
            } else if (!plugin.isEnabled()) {
                loadedBuilder.append(ChatColor.YELLOW).append(loadedPlugin);
            } else {
                loadedBuilder.append(ChatColor.GREEN).append(loadedPlugin);
            }
            loadedBuilder.append(ChatColor.RESET).append(", ");
        }

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&cFailed to Load &eFailed to Enable &aEnabled")
        );
        console.sendMessage(loadedBuilder.toString());
    }

    private void loadDefaultConfig() {
        getDataFolder().mkdirs();
        getConfig().options().copyDefaults(true);
        getConfig().options().header("PFS | Plugin Folder Swapper - Config\n"
                + "PluginPaths - Exact filepath to the plugins, can use %serverfolder%/blabla.. if can't see exact path."
        );
        saveConfig();
    }

    @Override
    public void onDisable() {
    }

    public void log(String message) {
        Logger logger = getLogger();
        logger.info(message);
    }

    public void logError(String message) {
        getLogger().severe(message);
    }

    private List<String> loadPlugins() {
        return new Loader(this).loadPlugins();
    }
}
