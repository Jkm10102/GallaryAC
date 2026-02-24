package dev.afonso.galleryac.check;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.data.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public abstract class AbstractCheck {
    protected final GalleryAC plugin;
    protected final PlayerData playerData;
    protected final CheckType checkType;

    protected double violations;
    protected double maxVl;
    protected boolean enabled;
    protected String banCommand;
    private int strictness;
    private int flagAfterViolations;
    private long resetViolationsAfterMs;

    private long lastFlagTime;
    private static final long FLAG_COOLDOWN = 300L;

    public AbstractCheck(GalleryAC plugin, PlayerData playerData, CheckType checkType) {
        this.plugin = plugin;
        this.playerData = playerData;
        this.checkType = checkType;
        this.loadConfig();
    }

    private void loadConfig() {
        String path = "checks." + checkType.getConfigName() + ".";
        this.enabled = plugin.getConfig().getBoolean(path + "enabled", true);
        this.maxVl = plugin.getConfig().getDouble(path + "max-vl", 10.0);
        this.banCommand = plugin.getConfig().getString(path + "ban-command", "kick %player% Cheating");
        this.strictness = plugin.getConfig().getInt("strictness", 3);
        this.flagAfterViolations = plugin.getConfig().getInt("global.flag-after-violations", 5);
        this.resetViolationsAfterMs = plugin.getConfig().getLong("global.reset-violations-after-minutes", 5) * 60000L;
    }

    public void reload() {
        this.loadConfig();
    }

    protected void fail(String details, double banVL, long cooldown) {
        if (!enabled) return;

        double strictnessMultiplier = getStrictnessMultiplier();
        long adjustedCooldown = (long) (cooldown / strictnessMultiplier);

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFlagTime < adjustedCooldown) {
            return;
        }

        this.lastFlagTime = currentTime;

        Player player = Bukkit.getPlayer(playerData.getUuid());
        if (player == null) return;

        int currentViolations = playerData.getCheckViolations().getOrDefault(checkType.getConfigName(), 0);
        Long lastVlTime = playerData.getLastViolationTime().get(checkType.getConfigName());

        if (lastVlTime != null && (currentTime - lastVlTime) > resetViolationsAfterMs) {
            currentViolations = 0;
        }

        currentViolations++;
        playerData.getCheckViolations().put(checkType.getConfigName(), currentViolations);
        playerData.getLastViolationTime().put(checkType.getConfigName(), currentTime);

        if (currentViolations >= flagAfterViolations) {
            violations += strictnessMultiplier;
            playerData.setTotalViolations(playerData.getTotalViolations() + 1);

            plugin.getCheckManager().sendAlert(player, checkType, violations, details);
            logViolation(player, details);

            if (violations >= banVL) {
                executePunishment(player);
            }
        }
    }

    private double getStrictnessMultiplier() {
        switch (strictness) {
            case 1: return 0.5;
            case 2: return 0.75;
            case 3: return 1.0;
            case 4: return 1.5;
            case 5: return 2.0;
            default: return 1.0;
        }
    }

    private void logViolation(Player player, String details) {
        File logFile = new File(plugin.getDataFolder(), "data.log");
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());

            String detailsClean = details != null ? details.replaceAll("§[0-9a-fk-or]", "").replaceAll("\n", " | ") : "";
            String logLine = String.format("[%s] %s (%s) flagged %s (VL: %.1f) - %s%n",
                    timestamp, player.getName(), player.getUniqueId(),
                    checkType.getConfigName(), violations, detailsClean);

            writer.write(logLine);
            writer.close();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write to data.log: " + e.getMessage());
        }
    }

    protected void decrease(double amount) {
        this.violations = Math.max(0.0, violations - amount);
    }

    private void executePunishment(Player player) {
        String command = banCommand.replace("%player%", player.getName());
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            plugin.getCheckManager().broadcastPunishment(player, checkType);
        });
    }

    protected double getBanVL() {
        return maxVl;
    }

    protected String format(int decimals, Number value) {
        return String.format("%." + decimals + "f", value.doubleValue());
    }

    public abstract void handle();
}