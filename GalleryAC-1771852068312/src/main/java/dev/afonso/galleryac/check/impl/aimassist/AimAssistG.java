package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.util.MathUtil;

public class AimAssistG extends AbstractCheck {

    private double lastDeltaYaw;
    private double lastDeltaYawAccel;
    private double lastFlagAccel;

    public AimAssistG(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_G);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.getAttackTicks() > 2) return;
        if (data.isTeleporting()) return;

        double deltaPitch = Math.abs(data.getDeltaPitch());
        double deltaYaw = Math.abs(data.getDeltaYaw());
        double yawAccel = Math.abs(deltaYaw - lastDeltaYaw);
        double accelDiff = Math.abs(yawAccel - lastDeltaYawAccel);
        double gcdYAW = MathUtil.gcd(deltaYaw, lastDeltaYaw);

        boolean invalidAim = deltaYaw > 1.0 && deltaYaw < 30.0 && deltaPitch < 25.0 && yawAccel < 0.0015;
        double addition = gcdYAW > 0.01 ? 1.0 : 0.75;
        double wildcard = yawAccel == lastFlagAccel ? 0.25 : 0.0;

        if (invalidAim) {
            violations = Math.min(violations + addition + wildcard, 10.0);
            if (violations > 5.0) {
                fail("* Consistent rotations\n §f* gcdY: §b" + gcdYAW +
                        "\n §f* A: §b" + yawAccel +
                        "\n §f* AD: §b" + accelDiff, getBanVL(), 300L);
            }

            lastFlagAccel = yawAccel;
        } else {
            violations = Math.max(violations - 0.5, 0.0);
        }

        lastDeltaYaw = deltaYaw;
        lastDeltaYawAccel = yawAccel;
    }
}