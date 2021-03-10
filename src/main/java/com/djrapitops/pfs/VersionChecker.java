package com.djrapitops.pfs;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class VersionChecker {

    public static String checkVersion() {
        PFS plugin = getPlugin(PFS.class);
        try {
            URL githubUrl = new URL("https://raw.githubusercontent.com/AuroraLS3/PluginFolderSwapper/master/PluginFolderSwapper/src/main/resources/plugin.yml");

            VersionNumber currentVersion = new VersionNumber(plugin.getDescription().getVersion());

            Optional<VersionNumber> newerVersion = readVersionLine(githubUrl)
                    .map(line -> line.split(": ")[1])
                    .map(VersionNumber::new)
                    .filter(version -> version.isNewerThan(currentVersion));

            return newerVersion.map(VersionChecker::getVersionNotification)
                    .orElse("You're running the latest version");
        } catch (Exception e) {
            return "Failed to get newest version number.";
        }
    }

    private static String getVersionNotification(VersionNumber versionNumber) {
        return "New Version (" + versionNumber.asString() + ") is availible at https://www.spigotmc.org/resources/pfs-plugin-folder-swapper.34336/";
    }

    private static Optional<String> readVersionLine(URL githubUrl) throws IOException {
        try (Scanner websiteScanner = new Scanner(githubUrl.openStream())) {
            while (websiteScanner.hasNextLine()) {
                String line = websiteScanner.nextLine();
                if (line.toLowerCase().contains("version")) {
                    return Optional.of(line);
                }
            }
        }
        return Optional.empty();
    }

}
