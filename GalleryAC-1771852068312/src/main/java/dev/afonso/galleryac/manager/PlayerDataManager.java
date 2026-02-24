package dev.afonso.galleryac.manager;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.data.PlayerData;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerDataManager implements Listener {
    private final GalleryAC plugin;
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public PlayerDataManager(GalleryAC plugin) {
        this.plugin = plugin;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public void createPlayerData(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        playerDataMap.put(uuid, data);
        plugin.getCheckManager().registerChecks(data);
    }

    public void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        createPlayerData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removePlayerData(event.getPlayer().getUniqueId());
    }

    public void tickAll() {
        for (PlayerData data : playerDataMap.values()) {
            data.tick();
        }
    }
}