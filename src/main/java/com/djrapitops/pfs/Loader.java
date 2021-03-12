package com.djrapitops.pfs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Loader {

    private final PluginLoader pluginLoader;
    private final PluginManager pluginManager;
    private final Logger logger;
    private final FileConfiguration config;

    private final List<String> loadedPluginNames;
    private final List<Plugin> loadedPlugins;
    private final String serverFolderPath;
    private Map<File, String> pluginNames;

    public Loader(PFS pfs) {
        logger = pfs.getLogger();
        config = pfs.getConfig();
        pluginLoader = pfs.getPluginLoader();
        pluginManager = pfs.getServer().getPluginManager();

        serverFolderPath = new File("").getAbsolutePath();
        logger.info("Server folder found at: " + serverFolderPath);

        loadedPlugins = new ArrayList<>();
        loadedPluginNames = Arrays.stream(pluginManager.getPlugins())
                .map(Plugin::getName)
                .collect(Collectors.toList());

    }

    public List<String> loadPlugins() {
        Set<File> files = findJarFiles(config.getStringList("PluginPaths"));
        if (files.isEmpty()) {
            logger.warning("No Plugins were found (is config set-up correctly?)");
            return new ArrayList<>();
        }

        return loadPlugins(getInLoadOrder(files));
    }

    private List<String> loadPlugins(List<File> files) {
        for (File file : files) {
            if (!loadedPluginNames.contains(pluginNames.get(file))) continue;
            tryToLoadFromFile(file);
        }

        for (Plugin plugin : loadedPlugins) {
            if (!plugin.isEnabled()) {
                pluginManager.enablePlugin(plugin);
            }
        }

        return loadedPluginNames;
    }

    private void tryToLoadFromFile(File file) {
        try {
            loadPluginFromFile(file);
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            logger.severe(e.toString());
        }
    }

    private void loadPluginFromFile(File pluginJar) throws InvalidDescriptionException, InvalidPluginException {
        try {
            Plugin plugin = pluginManager.loadPlugin(pluginJar);
            if (plugin != null) {
                loadedPlugins.add(plugin);
                String pluginName = plugin.getName();
                loadedPluginNames.add(pluginName);
                pluginNames.put(pluginJar, pluginName);
            }
        } catch (UnknownDependencyException e) {
            logger.severe(e.toString() + " while loading " + pluginJar.getName());
            e.printStackTrace();
        }
    }

    private List<File> getInLoadOrder(Set<File> files) {
        pluginNames = new HashMap<>();

        return files.stream()
                .sorted(this::loadOrderComparator)
                .collect(Collectors.toList());
    }

    private int loadOrderComparator(File plugin1, File plugin2) {
        try {
            PluginDescriptionFile p1Desc = pluginLoader.getPluginDescription(plugin1);
            PluginDescriptionFile p2Desc = pluginLoader.getPluginDescription(plugin2);
            return p1Desc.getLoad().compareTo(p2Desc.getLoad());
        } catch (InvalidDescriptionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private Set<File> findJarFiles(List<String> folderPaths) {
        Set<File> files = new HashSet<>();

        for (String path : folderPaths) {
            if (path.endsWith("ExamplePlugin-1.0.jar")) {
                continue;
            }

            Path filePath;
            if (path.contains("%serverfolder%")) {
                filePath = Paths.get(serverFolderPath).resolve(path.replace("%serverfolder%/", ""));
            } else {
                filePath = Paths.get(path);
            }

            if (filePath.endsWith(".jar")) {
                files.add(filePath.toFile());
            } else {
                findPluginsFromFolder(files, path, filePath);
            }
        }

        return files;
    }

    private void findPluginsFromFolder(Set<File> files, String path, Path filePath) {
        File folder = filePath.toFile();
        if (folder.isFile()) return;

        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null || listOfFiles.length == 0) {
            logger.severe("Incorrect/Empty/Non-Existent folder in config: " + path);
            return;
        }

        for (File pluginFile : listOfFiles) {
            if (pluginFile.isFile() && pluginFile.getName().contains(".jar")) {
                files.add(pluginFile);
            }
        }
    }
}