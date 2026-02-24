package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class AimAssistB extends AbstractCheck {

    private double bufferP;
    private double bufferY;

    public AimAssistB(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_B);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.recentlyTeleported(3)) return;

        float deltaPitch = data.getDeltaPitch();
        float deltaYaw = data.getDeltaYaw();

        float moduloYaw = deltaYaw % 1.0f;
        float moduloYaw2 = deltaYaw % 0.1f;
        float moduloYaw3 = deltaYaw % 0.05f;
        float roundYaw = (float) Math.round(data.getYaw());

        float moduloPitch = deltaPitch % 1.0f;
        float moduloPitch2 = deltaPitch % 0.1f;
        float moduloPitch3 = deltaPitch % 0.05f;
        float roundPitch = (float) Math.round(data.getPitch());

        if (Math.abs(data.getPitch()) < 90.0f && data.getPitch() > 0.0f && deltaPitch > 0.0f &&
                (moduloPitch == 0.0f || moduloPitch2 == 0.0f || moduloPitch3 == 0.0f || data.getPitch() == roundPitch)) {
            if (++bufferP > 10.0) {
                fail("* Rounded pitch\n §f* deltaPitch: §b" + deltaPitch +
                        "\n §f* moduloPitch: §b" + moduloPitch, getBanVL(), 300L);
            }
        } else {
            bufferP = Math.max(bufferP - 0.8, 0.0);
        }

        if (deltaYaw > 0.0f && (moduloYaw == 0.0f || moduloYaw2 == 0.0f || moduloYaw3 == 0.0f || data.getYaw() == roundYaw)) {
            if (++bufferY > 10.0) {
                fail("* Rounded yaw\n §f* deltaYaw: §b" + deltaYaw +
                        "\n §f* moduloYaw: §b" + moduloYaw, getBanVL(), 300L);
            }
        } else {
            bufferY = Math.max(bufferY - 1.0, 0.0);
        }
    }
}