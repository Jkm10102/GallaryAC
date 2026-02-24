package dev.afonso.galleryac.config;

import dev.afonso.galleryac.GalleryAC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class MessageLoader {
    private final GalleryAC plugin;
    private FileConfiguration messages;
    private File messagesFile;

    public MessageLoader(GalleryAC plugin) {
        this.plugin = plugin;
    }

    public void load() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            try {
                messagesFile.getParentFile().mkdirs();
                InputStream in = plugin.getResource("messages.yml");
                if (in != null) {
                    Files.copy(in, messagesFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String path) {
        String message = messages.getString(path, "");
        String prefix = messages.getString("prefix", "");
        return message.replace("%prefix%", prefix);
    }

    public String getPrefix() {
        return messages.getString("prefix", "");
    }

    public String getAlertFormat() {
        return getMessage("alerts.format");
    }

    public String getPunishBroadcast() {
        return getMessage("punish.broadcast");
    }
}