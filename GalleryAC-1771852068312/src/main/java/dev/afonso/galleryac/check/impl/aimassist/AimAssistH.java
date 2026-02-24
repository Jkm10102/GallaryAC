package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class AimAssistH extends AbstractCheck {

    public AimAssistH(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_H);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.getAttackTicks() >= 20) return;
        if (data.isTeleporting()) return;

        float deltaYaw = data.getDeltaYaw();

        if (data.getPitch() != 0.0f || data.getLastPitch() != 0.0f || deltaYaw <= 2.0f) {
            violations *= 0.8;
        } else if (++violations > 3.0) {
            fail("* Weird rotation\n §f* p: §b" + data.getPitch() +
                    "\n §f* lp: §b" + data.getLastPitch(), getBanVL(), 300L);
        }
    }
}