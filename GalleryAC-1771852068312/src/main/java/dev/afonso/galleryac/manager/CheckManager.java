package dev.afonso.galleryac.manager;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.check.impl.aimassist.*;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CheckManager {
    private final GalleryAC plugin;
    private final Set<UUID> alertsEnabled = new HashSet<>();
    
    public CheckManager(GalleryAC plugin) {
        this.plugin = plugin;
    }
    
    public void registerChecks(PlayerData playerData) {
        playerData.addCheck(new AimAssistA(plugin, playerData));
        playerData.addCheck(new AimAssistB(plugin, playerData));
        playerData.addCheck(new AimAssistC(plugin, playerData));
        playerData.addCheck(new AimAssistD(plugin, playerData));
        playerData.addCheck(new AimAssistE(plugin, playerData));
        playerData.addCheck(new AimAssistF(plugin, playerData));
        playerData.addCheck(new AimAssistG(plugin, playerData));
        playerData.addCheck(new AimAssistH(plugin, playerData));
        playerData.addCheck(new AimAssistI(plugin, playerData));
        playerData.addCheck(new AimAssistJ(plugin, playerData));
        playerData.addCheck(new AimAssistM(plugin, playerData));
        playerData.addCheck(new AimAssistN(plugin, playerData));
        playerData.addCheck(new AnalysisA(plugin, playerData));
        playerData.addCheck(new AnalysisB(plugin, playerData));
        playerData.addCheck(new AimRounded(plugin, playerData));
        playerData.addCheck(new Mouse(plugin, playerData));
        playerData.addCheck(new Sensitivity(plugin, playerData));
    }
    
    public void handleChecks(PlayerData playerData) {
        for (AbstractCheck check : playerData.getChecks()) {
            if (check.isEnabled()) {
                check.handle();
            }
        }
    }
    
    public void reloadChecks() {
        for (PlayerData data : plugin.getPlayerDataManager().getPlayerDataMap().values()) {
            for (AbstractCheck check : data.getChecks()) {
                check.reload();
            }
        }
    }
    
    public void sendAlert(Player player, CheckType checkType, double vl, String details) {
        String format = plugin.getMessageLoader().getAlertFormat();
        String message = format
                .replace("%player%", player.getName())
                .replace("%check%", checkType.getConfigName())
                .replace("%vl%", String.format("%.1f", vl))
                .replace("%details%", details != null ? "\n" + details : "");
        
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("galleryac.alerts") && alertsEnabled.contains(online.getUniqueId())) {
                online.sendMessage(ColorUtil.colorize(message));
            }
        }
    }
    
    public void broadcastPunishment(Player player, CheckType checkType) {
        String format = plugin.getMessageLoader().getPunishBroadcast();
        String message = format
                .replace("%player%", player.getName())
                .replace("%check%", checkType.getConfigName());
        
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("galleryac.alerts")) {
                online.sendMessage(ColorUtil.colorize(message));
            }
        }
    }
    
    public void toggleAlerts(Player player) {
        UUID uuid = player.getUniqueId();
        if (alertsEnabled.contains(uuid)) {
            alertsEnabled.remove(uuid);
            player.sendMessage(ColorUtil.colorize(plugin.getMessageLoader().getMessage("alerts-toggle.disabled")));
        } else {
            alertsEnabled.add(uuid);
            player.sendMessage(ColorUtil.colorize(plugin.getMessageLoader().getMessage("alerts-toggle.enabled")));
        }
    }
    
    public void enableAlerts(Player player) {
        alertsEnabled.add(player.getUniqueId());
    }
}