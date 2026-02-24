package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class AimAssistI extends AbstractCheck {

    private int zeroDeltaTicks;

    public AimAssistI(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_I);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.getAttackTicks() >= 3) return;
        if (data.isTeleporting()) return;

        float deltaYaw = data.getDeltaYaw();
        float deltaPitch = data.getDeltaPitch();

        if (deltaPitch == 0.0f) {
            ++zeroDeltaTicks;
        } else {
            zeroDeltaTicks = 0;
        }

        if (zeroDeltaTicks <= 40 || deltaYaw <= 3.0f || Math.abs(data.getPitch()) >= 45.0f) {
            violations *= 0.75;
        } else if (++violations > 5.0) {
            fail("* Weird rotation\n §f* p: §b" + data.getPitch() +
                    "\n §f* lp: §b" + data.getLastPitch(), getBanVL(), 300L);
        }
    }
}