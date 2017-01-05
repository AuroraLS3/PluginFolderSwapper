package com.djrapitops.pfs;

import java.net.URL;
import java.util.Scanner;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class MiscUtils {

    public static String checkVersion() {
        PFS plugin = getPlugin(PFS.class);
        String[] nVersion;
        String[] cVersion;
        String lineWithVersion;
        try {
            URL githubUrl = new URL("https://raw.githubusercontent.com/Rsl1122/PluginFolderSwapper/master/PluginFolderSwapper/src/plugin.yml");
            lineWithVersion = "";
            Scanner websiteScanner = new Scanner(githubUrl.openStream());
            while (websiteScanner.hasNextLine()) {
                String line = websiteScanner.nextLine();
                if (line.toLowerCase().contains("version")) {
                    lineWithVersion = line;
                    break;
                }
            }
            String versionString = lineWithVersion.split(": ")[1];
            nVersion = versionString.split("\\.");
            double newestVersionNumber = Double.parseDouble(nVersion[0] + "." + nVersion[1] + nVersion[2]);
            cVersion = plugin.getDescription().getVersion().split("\\.");
            double currentVersionNumber = Double.parseDouble(cVersion[0] + "." + cVersion[1] + cVersion[2]);
            if (newestVersionNumber > currentVersionNumber) {
                return "New Version (" + versionString + ") is availible at https://www.spigotmc.org/resources/authors/rsl1122.122894/";
            } else {
                return "You're running the latest version";
            }
        } catch (Exception e) {
            plugin.logError("Failed to compare versions.\n"+e);
        }
        return "Failed to get newest version number.";
    }

}
