package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.util.MathUtil;

public class AimAssistF extends AbstractCheck {

    private float lastDeltaPitch;
    private float lastDeltaYaw;
    private int streak;

    public AimAssistF(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_F);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.getAttackTicks() > 5) return;

        float deltaPitch = data.getDeltaPitch();
        float deltaYaw = data.getDeltaYaw();

        if (deltaYaw > 0.001f && deltaYaw <= 5.0f && lastDeltaYaw <= 5.0f && Math.abs(data.getPitch()) <= 80.0f) {
            double gcdYAW = MathUtil.getGcd(deltaYaw, lastDeltaYaw);

            if (gcdYAW < 0.009 && !data.isCinematic()) {
                double gcdPITCH = MathUtil.getGcd(deltaPitch, lastDeltaPitch);

                if (deltaPitch > 0.0f && gcdPITCH < 0.009) {
                    streak = 0;
                    violations = 0.0;
                }

                if (++streak > 20 && lastDeltaPitch == 0.0f && ++violations > 15.0) {
                    fail("* Consistent rotations\n §f* gcdY: §b" + gcdYAW +
                            "\n §f* gcdP: §b" + gcdPITCH, getBanVL(), 300L);
                    violations = 0.0;
                }
            } else {
                decrease(0.5);
            }
        }

        lastDeltaPitch = deltaPitch;
        lastDeltaYaw = deltaYaw;
    }
}