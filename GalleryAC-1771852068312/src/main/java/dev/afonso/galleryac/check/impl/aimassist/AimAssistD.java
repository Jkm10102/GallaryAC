package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class AimAssistD extends AbstractCheck {

    private float lastDeltaYaw;
    private float lastLastDeltaYaw;

    public AimAssistD(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_D);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.isTeleporting()) return;
        if (data.getAttackTicks() >= 4) return;

        float deltaYaw = data.getDeltaYaw();

        if (deltaYaw < 3.0f && lastDeltaYaw > 30.0f && lastLastDeltaYaw < 3.0f) {
            fail("* Snappy aim\n §f* now: §b" + deltaYaw +
                    "\n §f* l: §b" + lastDeltaYaw +
                    "\n §f* ll: §b" + lastLastDeltaYaw, getBanVL(), 100L);
        }

        lastLastDeltaYaw = lastDeltaYaw;
        lastDeltaYaw = deltaYaw;
    }
}