package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;

public class AimRounded extends AbstractCheck {

    private int buffer;
    private int legitStreak;

    public AimRounded(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ROUNDED);
    }

    @Override
    public void handle() {

        if (!enabled) return;

        MovementData data = playerData.getMovementData();

        // --- Basic Exempts ---
        if (data.isTeleporting() || data.recentlyTeleported(5)) {
            buffer = 0;
            legitStreak = 0;
            return;
        }

        if (data.isCinematic()) return;

        // Only check when rotating
        if (data.getDeltaYaw() == 0 && data.getDeltaPitch() == 0) return;

        float yaw = data.getYaw();
        float pitch = data.getPitch();

        float absYaw = Math.abs(yaw);
        float absPitch = Math.abs(pitch);

        // --- Rounded detection ---
        boolean roundedYaw =
                Math.round(yaw) == yaw &&
                        absYaw != 0.0f &&
                        absYaw != 180.0f;

        boolean roundedPitch =
                Math.round(pitch) == pitch &&
                        absPitch != 90.0f;

        boolean rounded = roundedYaw || roundedPitch;

        if (rounded) {

            legitStreak = 0;
            buffer++;

            if (buffer > 3) {

                violations += 1.0;

                fail(
                        "* Impossible rounded rotation\n" +
                                " §f* yaw: §b" + format(2, yaw) +
                                "\n §f* pitch: §b" + format(2, pitch),
                        getBanVL(),
                        400L
                );

                buffer = 2; // prevent instant stacking
            }

        } else {

            legitStreak++;

            // Rise-style decay after legit rotations
            if (legitStreak > 5) {
                buffer = Math.max(0, buffer - 1);
                decrease(0.25);
                legitStreak = 0;
            }
        }
    }
}