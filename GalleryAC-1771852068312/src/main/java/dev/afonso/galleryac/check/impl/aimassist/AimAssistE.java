package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.util.MathUtil;

public class AimAssistE extends AbstractCheck {

    private float lastDeltaPitch;
    private float lastDeltaYaw;
    private float lastGCD;

    public AimAssistE(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_E);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.getAttackTicks() >= 3) return;

        float deltaPitch = data.getDeltaPitch();
        float deltaYaw = data.getDeltaYaw();

        boolean canCheck = !data.isCinematic() &&
                Math.abs(data.getPitch()) < 90.0f &&
                Math.abs(data.getLastPitch()) < 90.0f &&
                deltaPitch <= 5.0f &&
                !data.recentlyTeleported(5);

        double addition = lastGCD < 0.003f ? 0.5 : 0.0;
        float gcdPITCH = MathUtil.getGcd(deltaPitch, lastDeltaPitch);
        float gcdYAW = MathUtil.getGcd(deltaYaw, lastDeltaYaw);

        if (canCheck) {
            if (deltaPitch > 0.2f && Math.abs(deltaPitch - lastDeltaPitch) > 0.2f && gcdPITCH < 0.008f) {
                violations = Math.min(30.0, violations + 0.5 + addition);
                if (violations > 17.5) {
                    fail("* Consistent rotations\n §f* gcd: §b" + gcdPITCH + " | " + gcdYAW +
                            "\n §f* deltaPitch: §b" + deltaPitch, getBanVL(), 300L);
                }
            } else {
                violations = Math.max(violations - 0.65, 0.0);
            }

            lastGCD = gcdPITCH;
        } else {
            violations = Math.max(violations - 1.1, 0.0);
        }

        lastDeltaPitch = deltaPitch;
        lastDeltaYaw = deltaYaw;
    }
}