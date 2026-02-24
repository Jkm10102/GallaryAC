package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class AnalysisA extends AbstractCheck {

    private double sensitivityBuffer = 0.0;
    private static final double FLAG_THRESHOLD = 3.0;

    public AnalysisA(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.ANALYSIS_A);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;

        // ---- JOIN STABILIZATION ----
        if (playerData.getTicksExisted() < 80) return;

        // ---- TELEPORT EXEMPT ----
        if (data.isTeleporting() || data.recentlyTeleported(5)) return;

        if (data.getAttackTicks() > 3) return;

        double deltaPitch = data.getDeltaPitch();
        if (deltaPitch < 0.02) return;

        // ---- REQUIRE STABLE GCD ----
        if (data.getPitchGCD() <= 0 || data.getLastPitchGCD() <= 0) return;

        double finalSensitivity = data.getFinalSensitivity();
        if (finalSensitivity <= 0) return;

        boolean tooLowSensitivity = data.hasTooLowSensitivity();
        boolean invalid = !data.hasValidSensitivityNormalized() && !tooLowSensitivity;

        String pitch = String.format("%.5f", deltaPitch);
        String info = "send: " + finalSensitivity + " pitch: " + pitch;

        if (invalid) {

            sensitivityBuffer += 1.0;

            if (sensitivityBuffer >= FLAG_THRESHOLD) {
                fail(info, getBanVL(), 300L);
                sensitivityBuffer = 0.0;
            }

        } else {
            sensitivityBuffer = Math.max(0.0, sensitivityBuffer - 0.5);
        }
    }
}