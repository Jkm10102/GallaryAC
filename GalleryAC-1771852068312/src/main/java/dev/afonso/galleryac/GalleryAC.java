package dev.afonso.galleryac;

import com.github.retrooper.packetevents.PacketEvents;
import dev.afonso.galleryac.config.ConfigLoader;
import dev.afonso.galleryac.config.MessageLoader;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.manager.CheckManager;
import dev.afonso.galleryac.manager.PlayerDataManager;
import dev.afonso.galleryac.util.ColorUtil;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@Getter
public class GalleryAC extends JavaPlugin {
    
    private PlayerDataManager playerDataManager;
    private CheckManager checkManager;
    private ConfigLoader configLoader;
    private MessageLoader messageLoader;
    
    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .checkForUpdates(false)
                .bStats(false);
        PacketEvents.getAPI().load();
    }
    
    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        
        this.configLoader = new ConfigLoader(this);
        this.messageLoader = new MessageLoader(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.checkManager = new CheckManager(this);
        
        configLoader.load();
        messageLoader.load();
        
        Bukkit.getPluginManager().registerEvents(playerDataManager, this);
        
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListenerAbstract(PacketListenerPriority.NORMAL) {
            @Override
            public void onPacketReceive(PacketReceiveEvent event) {
                handlePacket(event);
            }
        });
        
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            playerDataManager.tickAll();
        }, 1L, 1L);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerDataManager.createPlayerData(player.getUniqueId());
        }
        
        getLogger().info("GalleryAC has been enabled!");
    }
    
    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        getLogger().info("GalleryAC has been disabled!");
    }
    
    private void handlePacket(PacketReceiveEvent event) {
        Object channel = event.getUser().getChannel();
        if (channel == null) return;
        
        Player player = (Player) event.getPlayer();
        if (player == null) return;
        
        PlayerData data = playerDataManager.getPlayerData(player.getUniqueId());
        if (data == null) return;
        
        data.updatePacketTime();
        
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION ||
            event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION ||
            event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION) {
            
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
            
            if (flying.hasRotationChanged()) {
                MovementData movementData = data.getMovementData();
                movementData.updateRotation(flying.getLocation().getYaw(), flying.getLocation().getPitch());
                checkManager.handleChecks(data);
            }
        } else if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
            WrapperPlayClientInteractEntity interact = new WrapperPlayClientInteractEntity(event);
            
            if (interact.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                data.getMovementData().incrementHit();
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("galleryac")) {
            return false;
        }
        
        if (!sender.hasPermission("galleryac.admin")) {
            sender.sendMessage(ColorUtil.colorize("&cYou don't have permission to use this command."));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ColorUtil.colorize("&7&m----------------------------"));
            sender.sendMessage(ColorUtil.colorize("&#00FFAA&lGalleryAC &7v1.0.0"));
            sender.sendMessage(ColorUtil.colorize("&7/galleryac reload &8- &7Reload configuration"));
            sender.sendMessage(ColorUtil.colorize("&7/galleryac alerts &8- &7Toggle alerts"));
            sender.sendMessage(ColorUtil.colorize("&7&m----------------------------"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            configLoader.reload();
            messageLoader.reload();
            checkManager.reloadChecks();
            sender.sendMessage(ColorUtil.colorize(messageLoader.getMessage("reload.success")));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("alerts")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ColorUtil.colorize("&cOnly players can use this command."));
                return true;
            }
            
            Player player = (Player) sender;
            if (!player.hasPermission("galleryac.alerts")) {
                player.sendMessage(ColorUtil.colorize("&cYou don't have permission to use this command."));
                return true;
            }
            
            checkManager.toggleAlerts(player);
            return true;
        }
        
        sender.sendMessage(ColorUtil.colorize("&cUnknown subcommand. Use /galleryac for help."));
        return true;
    }
}