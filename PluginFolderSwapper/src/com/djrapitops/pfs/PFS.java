package com.djrapitops.pfs;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Rsl1122
 */
public class PFS extends JavaPlugin {

    private Set<Plugin> enabledPlugins;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        getConfig().options().copyDefaults(true);

        getConfig().options().header("PFS | Plugin Folder Swapper - Config\n"
                + "PluginPaths - Exact filepath to the plugins"
        );

        saveConfig();
        enabledPlugins = new HashSet<>();

        log(MiscUtils.checkVersion());
        log("Plugin Folder Swapper enabled.");
        log("Loading plugins..");
        loadPlugins();
        if (enabledPlugins.isEmpty()) {
            logError("No Plugins were loaded (is config set-up correctly?) Disabling plugin..");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        log("Plugins loaded.");
    }

    @Override
    public void onDisable() {
        if (!enabledPlugins.isEmpty()) {
            unloadPlugins();
        }
        log("Plugin Folder Swapper disabled.");
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public void logError(String message) {
        getLogger().severe(message);
    }

    private void loadPlugins() {
        List<String> pluginList = getConfig().getStringList("PluginPaths");
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        for (String filePath : pluginList) {
            if (filePath.equals("C:/ExamplePath/ExamplePlugin-1.0.jar")) {
                continue;
            }
            try {
                File pluginJar = new File(filePath);
                Plugin plugin = pluginManager.loadPlugin(pluginJar);
                if (plugin != null) {
                    enabledPlugins.add(plugin);
                    pluginManager.enablePlugin(plugin);
                }
            } catch (InvalidPluginException | InvalidDescriptionException | UnknownDependencyException ex) {
                Logger.getLogger(PFS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void unloadPlugins() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        for (Plugin plugin : enabledPlugins) {
            pluginManager.disablePlugin(plugin);
        }
    }
}
