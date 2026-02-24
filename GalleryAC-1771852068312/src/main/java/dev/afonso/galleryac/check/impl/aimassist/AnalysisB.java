package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

import java.util.Deque;
import java.util.LinkedList;

public class AnalysisB extends AbstractCheck {

    private final Deque<Double> recentDeltas = new LinkedList<>();
    private double sensitivityBuffer = 0.0;

    private static final int BUFFER_SIZE = 10;
    private static final double FLAG_THRESHOLD = 3.0;

    public AnalysisB(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.ANALYSIS_B);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;

        // ---- JOIN STABILIZATION ----
        if (playerData.getTicksExisted() < 100) return;

        // ---- LAG SPIKE PROTECTION ----
        if (playerData.getTimeSinceLastPacket() > 150L) return;

        // ---- TELEPORT EXEMPT ----
        if (data.isTeleporting() || data.recentlyTeleported(5)) {
            recentDeltas.clear();
            sensitivityBuffer = 0;
            return;
        }

        // ---- CINEMATIC EXEMPT ----
        if (data.isCinematic() || data.getCinematicTicks() < 20) {
            recentDeltas.clear();
            sensitivityBuffer = 0;
            return;
        }

        // ---- EARLY COMBAT STABILIZATION ----
        if (data.getHitCount() < 3) return;

        if (data.getAttackTicks() > 5) return;

        double deltaPitch = data.getDeltaPitch();
        if (deltaPitch < 0.02) return;

        // ---- REQUIRE STABLE GCD ----
        if (data.getPitchGCD() <= 0 || data.getLastPitchGCD() <= 0) return;

        if (data.getPitchGCD() < 0.01f) {
            data.setFinalSensitivity(-1.0f);
            return;
        }

        double sensitivity = data.getFinalSensitivity();
        if (sensitivity <= 0) return;

        boolean tooLowSensitivity = data.hasTooLowSensitivity();
        boolean invalidSensitivity = !data.hasValidSensitivityNormalized();

        recentDeltas.add(deltaPitch);
        if (recentDeltas.size() > BUFFER_SIZE) {
            recentDeltas.poll();
        }

        if (recentDeltas.size() < 6) return;

        double mean = recentDeltas.stream().mapToDouble(d -> d).average().orElse(0.0);
        double deviation = recentDeltas.stream().mapToDouble(d -> Math.abs(d - mean)).average().orElse(0.0);

        if (invalidSensitivity && !tooLowSensitivity && deviation > 1.0E-4) {
            sensitivityBuffer += 1.0;

            if (sensitivityBuffer >= FLAG_THRESHOLD) {
                String info = String.format(
                        "send=%.5f pitch=%.5f deviation=%.5f",
                        sensitivity, deltaPitch, deviation
                );

                fail(info, getBanVL(), 300L);
                sensitivityBuffer = 0.0;
            }

        } else {
            sensitivityBuffer = Math.max(0.0, sensitivityBuffer - 0.5);
        }
    }
}