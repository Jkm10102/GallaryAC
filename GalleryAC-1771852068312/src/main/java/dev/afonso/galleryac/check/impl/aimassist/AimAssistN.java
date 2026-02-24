package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class AimAssistN extends AbstractCheck {

    private float pitch;
    private float yaw;

    public AimAssistN(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_N);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.getAttackTicks() > 3) return;
        if (data.isTeleporting()) return;

        float deltaPitch = Math.abs(data.getPitch() - data.getLastPitch());
        float deltaYaw = Math.abs(data.getYaw() - data.getLastYaw());
        float changeY = Math.abs(this.pitch - deltaPitch);
        float changeX = Math.abs(this.yaw - deltaYaw);
        double differenceYX = Math.abs(changeY - changeX);

        if (differenceYX <= 2.5 || deltaYaw >= 0.001 || this.yaw >= 0.001) {
            decrease(0.5);
        } else if (++violations > 8.0) {
            fail("* Weird X/Y changes\n §f* difference: §b" + format(4, differenceYX) +
                    "\n §f* change: §b" + format(4, changeY), getBanVL(), 300L);
        }

        this.pitch = deltaPitch;
        this.yaw = deltaYaw;
    }
}