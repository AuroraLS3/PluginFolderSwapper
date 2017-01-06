
package com.djrapitops.pfs;

import java.io.File;
import java.util.List;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Rsl1122
 */
public class API {
    private PFS plugin;

    public API(PFS plugin) {
        this.plugin = plugin;
    }
    
    // Plugin Config Specific methods, will use plugin config.
    
    /* loadPlugins
    Loads all plugins listed in the config file
    Plugins will be added to a set that is used in other methods.
    
    !! Does not enable the plugins!
    */
    public void loadPlugins() {
        plugin.loadPlugins();
    }
    
    public List<String> getFilePathList() {
        return plugin.getConfig().getStringList("PluginPaths");
    }
    
    public void setFilePathList(List<String> list) {
        plugin.getConfig().set("PluginPaths", list);
    }
    
    // Methods using the internal set of Plugins
    
    // Enables all plugins on the set
    public void enablePlugins() {
        plugin.enablePlugins();
    }
    
    // Enables the plugin and adds it on the internal set
    public void enablePlugin(Plugin toEnable) {
        plugin.enablePlugin(toEnable);
    }
    
    /* Loads the plugin from a file and adds it on the internal set
        !! Does not enable the plugin!
    */
    public void loadPluginFromFile(File pluginJar) {
        plugin.loadPluginFromFile(pluginJar);
    }
    
    /* Loads the plugin from file and adds it on the internal set
        !! Loads all plugins on the internal set!
    */
    public void enablePluginFromFile(File pluginJar) {
        loadPluginFromFile(pluginJar);
        enablePlugins();
    }
    
    /* Disables all plugins from the internal set
        !! Does not remove plugins from the set!
    */
    public void disablePlugins() {
        plugin.disablePlugins();
    }
    
    // Disables the plugin
    public void disablePlugin(Plugin toDisable) {
        plugin.disablePlugin(toDisable);
    }
    
    // Removes the plugin from the internal set
    public void unloadPlugin(Plugin toUnload) {
        plugin.removePlugin(toUnload);
    }
    
    // Reloads all plugins on the internal set
    public void reloadPlugins() {
        disablePlugins();
        enablePlugins();
    }
}
