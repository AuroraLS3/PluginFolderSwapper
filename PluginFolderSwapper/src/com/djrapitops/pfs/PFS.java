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
    private API api;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        getConfig().options().copyDefaults(true);

        getConfig().options().header("PFS | Plugin Folder Swapper - Config\n"
                + "PluginPaths - Exact filepath to the plugins, can use %serverfolder%/blabla.. if can't see excact path."
        );

        saveConfig();
        enabledPlugins = new HashSet<>();

        log(MiscUtils.checkVersion());
        log("Plugin Folder Swapper enabled.");
        log("-- Loading plugins.. --");
        loadPlugins();
        if (enabledPlugins.isEmpty()) {
            logError("No Plugins were loaded (is config set-up correctly?) Disabling plugin..");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        enablePlugins();
        api = new API(this);
        log("-- Plugins loaded. --");
    }

    @Override
    public void onDisable() {
        if (!enabledPlugins.isEmpty()) {
            disablePlugins();
        }
        log("Plugin Folder Swapper disabled.");
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public void logError(String message) {
        getLogger().severe(message);
    }

    void loadPlugins() {
        List<String> pluginList = getConfig().getStringList("PluginPaths");
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        for (String filePath : pluginList) {
            String fP = filePath.replaceAll("%serverfolder%/", new File(".").getAbsolutePath());
            if (fP.equals("C:/ExamplePath/ExamplePlugin-1.0.jar")) {
                continue;
            }
            if (fP.contains(".jar")) {
                File pluginJar = new File(fP);
                loadPluginFromFile(pluginManager, pluginJar);
            } else {
                File folder = new File(fP);
                File[] listOfFiles = folder.listFiles();
                if (listOfFiles.length == 0) {
                    logError("Incorrect/Empty/Non-Existent folder in config: "+filePath);
                    continue;
                }
                for (File pluginFile : listOfFiles) {
                    if (pluginFile.isFile()) {
                        loadPluginFromFile(pluginManager, pluginFile);
                    }
                }
            }
        }
    }

    private void loadPluginFromFile(PluginManager pluginManager, File pluginJar) {
        try {
            Plugin plugin = pluginManager.loadPlugin(pluginJar);
            if (plugin != null) {
                enabledPlugins.add(plugin);
            }
        } catch (InvalidPluginException | InvalidDescriptionException | UnknownDependencyException ex) {
            Logger.getLogger(PFS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void loadPluginFromFile(File pluginJar) {
        loadPluginFromFile(Bukkit.getServer().getPluginManager(), pluginJar);
    }

    void enablePlugins() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        for (Plugin plugin : enabledPlugins) {            
            pluginManager.enablePlugin(plugin);
        }
    }

    void disablePlugins() {
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        for (Plugin plugin : enabledPlugins) {
            pluginManager.disablePlugin(plugin);
        }
    }

    void disablePlugin(Plugin plugin) {
        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
    }

    void enablePlugin(Plugin plugin) {
        Bukkit.getServer().getPluginManager().enablePlugin(plugin);
        enabledPlugins.add(plugin);
    }

    public API getApi() {
        return api;
    }

    void removePlugin(Plugin plugin) {
        enabledPlugins.remove(plugin);
    }
}
