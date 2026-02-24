package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.util.MathUtil;

public class AimAssistC extends AbstractCheck {

    private double pitch;
    private double yaw;
    private double thresholdP;
    private double thresholdY;

    public AimAssistC(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_C);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.isTeleporting() || data.recentlyTeleported(3)) return;
        if (data.getSensitivity() >= 150) return;

        float deltaPitch = data.getDeltaPitch();
        float deltaYaw = data.getDeltaYaw();

        double gcdP = MathUtil.gcd(deltaPitch, this.pitch);
        double gcdY = MathUtil.gcd(deltaYaw, this.yaw);

        if (gcdP > 0.7 && (deltaPitch % this.pitch == 0.0 || Double.isNaN(deltaPitch % this.pitch)) && deltaPitch <= 10.0f) {
            thresholdP = Math.min(10.0, thresholdP + 0.5);
            if (thresholdP > 8.0) {
                fail("* Vertical aimassist\n §f* GCD: §b" + gcdP +
                        "\n §f* COUNT: §b" + thresholdP, getBanVL(), 150L);
            }
        } else {
            thresholdP = Math.max(0.0, thresholdP - 1.25);
        }

        if (gcdY > 0.7 && (deltaYaw % this.yaw == 0.0 || Double.isNaN(deltaYaw % this.yaw)) && deltaYaw <= 10.0f) {
            thresholdY = Math.min(10.0, thresholdY + 0.5);
            if (thresholdY > 4.0) {
                fail("* Horizontal aimassist\n §f* GCD: §b" + gcdY +
                        "\n §f* COUNT: §b" + thresholdY, getBanVL(), 150L);
            }
        } else {
            thresholdY = Math.max(0.0, thresholdY - 1.5);
        }

        this.pitch = deltaPitch;
        this.yaw = deltaYaw;
    }
}