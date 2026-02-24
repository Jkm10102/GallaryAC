package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class AimAssistJ extends AbstractCheck {

    public AimAssistJ(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_J);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.getSensitivityY() == -1.0f) return;
        if (data.getAttackTicks() > 1) return;
        if (data.recentlyTeleported(5)) return;

        float fixedYaw = fixedSensitivity(data.getSensitivityY(), data.getYaw());
        float fixedPitch = fixedSensitivity(data.getSensitivityY(), data.getPitch());
        float diffYaw = Math.abs(data.getYaw() - fixedYaw);
        float diffPitch = Math.abs(data.getPitch() - fixedPitch);

        if (Math.abs(data.getYaw() - fixedYaw) != 0.0f && Math.abs(data.getPitch() - fixedPitch) != 0.0f) {
            decrease(0.75);
        } else if (++violations > 10.0) {
            fail("* Round gcd patch\n §f* diffYaw: §b" + diffYaw +
                    "\n §f* diffPitch: §b" + diffPitch, getBanVL(), 300L);
        }
    }

    private float fixedSensitivity(float sensitivity, float angle) {
        float f = sensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        return angle - angle % gcd;
    }
}