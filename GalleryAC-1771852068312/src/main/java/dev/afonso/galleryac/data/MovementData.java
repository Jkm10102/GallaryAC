package dev.afonso.galleryac.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovementData {
    private float lastLastYaw;
    private float lastYaw;
    private float yaw;

    private float lastLastPitch;
    private float lastPitch;
    private float pitch;

    private float deltaYaw;
    private float deltaPitch;
    private float lastDeltaYaw;
    private float lastDeltaPitch;

    private double yawAccel;
    private double pitchAccel;
    private double lastYawAccel;
    private double lastPitchAccel;

    private float yawGCD;
    private float pitchGCD;
    private float lastYawGCD;
    private float lastPitchGCD;

    private int attackTicks;
    private int swingTicks;
    private int hitCount;
    private int swingCount;

    private int teleportTicks;
    private boolean teleporting;

    private int cinematicTicks;
    private boolean cinematic;

    private float sensitivity = -1.0f;
    private float sensitivityY = -1.0f;
    private float pitchMode;
    private float smallestRotationGCD = Float.MAX_VALUE;
    private float finalSensitivity = -1.0f;

    public void updateRotation(float newYaw, float newPitch) {
        this.lastLastYaw = this.lastYaw;
        this.lastLastPitch = this.lastPitch;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.yaw = newYaw;
        this.pitch = newPitch;

        this.lastDeltaYaw = this.deltaYaw;
        this.lastDeltaPitch = this.deltaPitch;

        this.deltaYaw = Math.abs(newYaw - lastYaw);
        this.deltaPitch = Math.abs(newPitch - lastPitch);

        this.lastYawAccel = this.yawAccel;
        this.lastPitchAccel = this.pitchAccel;

        this.yawAccel = Math.abs(deltaYaw - lastDeltaYaw);
        this.pitchAccel = Math.abs(deltaPitch - lastDeltaPitch);

        if (deltaYaw > 0 && lastDeltaYaw > 0) {
            this.lastYawGCD = this.yawGCD;
            this.yawGCD = gcd(deltaYaw, lastDeltaYaw);
        }

        if (deltaPitch > 0 && lastDeltaPitch > 0) {
            this.lastPitchGCD = this.pitchGCD;
            this.pitchGCD = gcd(deltaPitch, lastDeltaPitch);
        }
    }

    private float gcd(float a, float b) {
        if (a < b) return gcd(b, a);
        if (Math.abs(b) < 0.001f) return a;
        return gcd(b, a - (float) Math.floor(a / b) * b);
    }

    public void incrementSwing() {
        this.swingCount++;
        this.swingTicks = 0;
    }

    public void incrementHit() {
        this.hitCount++;
        this.attackTicks = 0;
    }

    public void setTeleporting() {
        this.teleporting = true;
        this.teleportTicks = 0;
    }

    public void tick() {
        this.attackTicks++;
        this.swingTicks++;
        this.teleportTicks++;
        this.cinematicTicks++;

        if (teleportTicks > 10) {
            this.teleporting = false;
        }

        if (cinematicTicks > 40) {
            this.cinematic = false;
        }
    }

    public boolean recentlyTeleported(int ticks) {
        return teleportTicks <= ticks;
    }

    public boolean hasTooLowSensitivity() {
        return finalSensitivity > 0 && finalSensitivity < 30.0f;
    }

    public double getFinalSensitivity() {
        if (finalSensitivity < 0 && pitchGCD > 0) {
            finalSensitivity = (float) ((pitchGCD / 0.15) * 100.0);
        }
        return finalSensitivity;
    }

    public boolean hasValidSensitivityNormalized() {
        if (pitchGCD <= 0 || deltaPitch <= 0) return true;
        
        double expectedGCD = 0.15 * (getFinalSensitivity() / 100.0);
        if (expectedGCD <= 0) return true;
        
        double normalizedDelta = deltaPitch / expectedGCD;
        double remainder = Math.abs(normalizedDelta - Math.round(normalizedDelta));
        
        return remainder < 0.01 || deltaPitch < 0.1f;
    }
}