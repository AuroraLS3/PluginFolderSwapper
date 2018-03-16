package com.djrapitops.pfs;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * @author Rsl1122
 */
public class PFS extends JavaPlugin {

    private List<String> loadedPlugins;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        getConfig().options().copyDefaults(true);

        getConfig().options().header("PFS | Plugin Folder Swapper - Config\n"
                + "PluginPaths - Exact filepath to the plugins, can use %serverfolder%/blabla.. if can't see exact path."
        );

        saveConfig();

        log(MiscUtils.checkVersion());
        log("Plugin Folder Swapper enabled.");
        log("-- Loading plugins.. --");
        loadedPlugins = loadPlugins();
        StringBuilder loadedBuilder = new StringBuilder("Loaded " + loadedPlugins.size() + " plugins: ");

        PluginManager pluginManager = getServer().getPluginManager();
        for (String loadedPlugin : loadedPlugins) {
            Plugin plugin = pluginManager.getPlugin(loadedPlugin);
            if (plugin == null) {
                loadedBuilder.append("§c").append(loadedPlugin);
            } else if (!plugin.isEnabled()) {
                loadedBuilder.append("§e").append(loadedPlugin);
            } else {
                loadedBuilder.append("§a").append(loadedPlugin);
            }
            loadedBuilder.append("§r, ");
        }

        log("§cFailed to Load §eFailed to Enable §aEnabled");
        log(loadedBuilder.toString());
        log("-- Plugins loaded. --");
    }

    @Override
    public void onDisable() {
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public void logError(String message) {
        getLogger().severe(message);
    }

    private List<String> loadPlugins() {
        return new PluginLoader(this).loadPlugins();
    }
}
