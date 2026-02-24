package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class Mouse extends AbstractCheck {

    private double deltaX;
    private double deltaY;
    private float lastPitch;
    private float lastPitchAccel;
    private float lastYaw;
    private float lastYawAccel;
    private int ticks;

    public Mouse(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.MOUSE);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;

        this.deltaX = Math.abs(data.getDeltaYaw());
        this.deltaY = Math.abs(data.getDeltaPitch());
        double deltaYAccel = Math.abs(this.deltaY - this.lastPitch);
        double deltaXAccel = Math.abs(this.deltaX - this.lastYaw);

        if (!isNearlySame(this.deltaY, this.lastPitch) &&
                !isNearlySame(deltaYAccel, this.lastPitchAccel) &&
                !isNearlySame(this.deltaX, deltaXAccel) &&
                !isNearlySame(deltaXAccel, this.lastYawAccel)) {
            ticks = Math.max(ticks - 1, 0);
            if (ticks <= 1) {
                data.setCinematic(false);
                data.setCinematicTicks(0);
            }
        } else {
            ticks = Math.min(80, ticks + 1);
            if (ticks >= 3) {
                data.setCinematic(true);
            }
        }

        this.lastPitch = (float) this.deltaY;
        this.lastPitchAccel = (float) deltaYAccel;
        this.lastYaw = (float) this.deltaX;
        this.lastYawAccel = (float) deltaXAccel;
    }

    private boolean isNearlySame(double d1, double d2) {
        MovementData md = playerData.getMovementData();
        float sens = md.getSensitivity();
        float sensY = md.getSensitivityY();
        double max = sens >= 100
                ? 0.0425 * sensY * 3.1
                : (sens >= 160 ? 0.07 * sensY * 3.2 : 0.0325);
        if (sens >= 160 && Math.abs(d1 - d2) > 1.0 && Math.abs(d1 - d2) < 8.0) {
            return true;
        }
        return Math.abs(d1 - d2) < max && Math.abs(d1 - d2) > 0.0015;
    }
}