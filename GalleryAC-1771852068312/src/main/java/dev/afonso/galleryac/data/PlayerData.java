package dev.afonso.galleryac.data;

import dev.afonso.galleryac.check.AbstractCheck;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    private final UUID uuid;
    private final MovementData movementData;
    private final List<AbstractCheck> checks;

    private long lastPacketTime;
    private int totalViolations;

    private int ticksExisted; // <-- ADD THIS

    private final Map<String, Integer> checkViolations = new HashMap<>();
    private final Map<String, Long> lastViolationTime = new HashMap<>();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.movementData = new MovementData();
        this.checks = new ArrayList<>();
        this.lastPacketTime = System.currentTimeMillis();
        this.ticksExisted = 0; // <-- INIT
    }

    public void addCheck(AbstractCheck check) {
        this.checks.add(check);
    }

    public void tick() {
        this.movementData.tick();
        this.ticksExisted++; // <-- INCREMENT EVERY TICK
    }

    public long getTimeSinceLastPacket() {
        return System.currentTimeMillis() - lastPacketTime;
    }

    public void updatePacketTime() {
        this.lastPacketTime = System.currentTimeMillis();
    }
}