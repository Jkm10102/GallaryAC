package dev.afonso.galleryac.config;

import dev.afonso.galleryac.GalleryAC;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLoader {
    private final GalleryAC plugin;

    public ConfigLoader(GalleryAC plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public boolean isCheckEnabled(String checkName) {
        return plugin.getConfig().getBoolean("checks." + checkName + ".enabled", true);
    }

    public int getMaxVL(String checkName) {
        return plugin.getConfig().getInt("checks." + checkName + ".max-vl", 10);
    }

    public String getBanCommand(String checkName) {
        return plugin.getConfig().getString("checks." + checkName + ".ban-command", "kick %player% Cheating");
    }
}