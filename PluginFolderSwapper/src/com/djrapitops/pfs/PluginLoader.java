package com.djrapitops.pfs;

import org.bukkit.plugin.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * //TODO Class Javadoc Comment
 *
 * @author Rsl1122
 */
public class PluginLoader {

    private final PFS pfs;
    private final org.bukkit.plugin.PluginLoader pluginLoader;
    private final PluginManager pluginManager;

    private List<String> loadedPluginNames;
    private List<Plugin> loadedPlugins;
    private Map<File, Set<String>> dependencies;
    private Map<File, String> pluginNames;

    private Set<String> afterWorld;

    public PluginLoader(PFS pfs) {
        this.pfs = pfs;
        pluginLoader = pfs.getPluginLoader();
        pluginManager = pfs.getServer().getPluginManager();

        loadedPlugins = new ArrayList<>();
        loadedPluginNames = new ArrayList<>();

        afterWorld = new HashSet<>();
    }

    public List<String> loadPlugins() {
        Set<File> files = loadJarFiles(pfs.getConfig().getStringList("PluginPaths"));
        if (files.isEmpty()) {
            pfs.logError("No Plugins were found (is config set-up correctly?)");
            return new ArrayList<>();
        }

        return loadPlugins(getLoadOrder(files));
    }

    private List<String> loadPlugins(List<File> files) {
        files.stream()
                .filter(file -> !loadedPluginNames.contains(pluginNames.get(file)))
                .forEach(file -> {
                    try {
                        loadPluginFromFile(file);
                    } catch (InvalidPluginException e) {
                    } catch (InvalidDescriptionException e) {

                    }
                });
        for (Plugin plugin : loadedPlugins) {
            if (!plugin.isEnabled()) {
                pluginManager.enablePlugin(plugin);
            }
        }

        return loadedPluginNames;
    }

    private void loadOnesWithoutDependencies() {
        dependencies.entrySet().stream().filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .forEach(file -> {
                    try {
                        loadPluginFromFile(file);
                    } catch (InvalidPluginException e) {
                    } catch (InvalidDescriptionException e) {

                    }
                });
    }

    private void loadPluginFromFile(File pluginJar) throws InvalidDescriptionException, InvalidPluginException {
        try {
            Plugin plugin = pluginManager.loadPlugin(pluginJar);
            if (plugin != null) {
                loadedPlugins.add(plugin);
                loadedPluginNames.add(plugin.getName());
            }
        } catch (UnknownDependencyException e) {
            pfs.log(e.toString() + " while loading " + pluginJar.getName());
            e.printStackTrace();
        }
    }

    private List<File> getLoadOrder(Set<File> files) {
        dependencies = new HashMap<>();
        pluginNames = new HashMap<>();

        return files.stream()
                .sorted((plugin1, plugin2) -> {
                    try {
                        PluginDescriptionFile p1Desc = pluginLoader.getPluginDescription(plugin1);
                        PluginDescriptionFile p2Desc = pluginLoader.getPluginDescription(plugin2);
                        return p1Desc.getLoad().compareTo(p2Desc.getLoad());
                    } catch (InvalidDescriptionException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }).collect(Collectors.toList());
    }

    private Set<File> loadJarFiles(List<String> folderPaths) {
        Set<File> files = new HashSet<>();

        for (String path : folderPaths) {
            if (path.contains("ExamplePlugin-1.0.jar")) {
                continue;
            }
            String fP = path.replaceAll("%serverfolder%/", new File(".").getAbsolutePath());

            if (fP.contains(".jar")) {
                files.add(new File(fP));
            } else {
                File folder = new File(fP);
                File[] listOfFiles = folder.listFiles();
                if (listOfFiles == null || listOfFiles.length == 0) {
                    pfs.logError("Incorrect/Empty/Non-Existent folder in config: " + path);
                    continue;
                }
                for (File pluginFile : listOfFiles) {
                    if (pluginFile.isFile() && pluginFile.getName().contains(".jar")) {
                        files.add(pluginFile);
                    }
                }
            }
        }

        return files;
    }
}