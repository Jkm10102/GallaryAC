package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.util.EvictingList;
import dev.afonso.galleryac.util.MathUtil;

public class AimAssistA extends AbstractCheck {

    private double lastAveragePitch;
    private double lastAverageYaw;
    private final EvictingList<Float> samplesP = new EvictingList<>(20);
    private final EvictingList<Float> samplesY = new EvictingList<>(20);

    public AimAssistA(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_A);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.isTeleporting() || data.recentlyTeleported(3)) return;
        if (data.getAttackTicks() > 20) return;

        float deltaYaw = data.getDeltaYaw();
        float deltaPitch = data.getDeltaPitch();

        if (deltaYaw > 0.0f) {
            samplesY.add(deltaYaw);
        }

        if (deltaPitch > 0.0f) {
            samplesP.add(deltaPitch);
        }

        if (samplesP.size() == 20 && samplesY.size() == 20) {
            double averagePitch = samplesP.iterator().hasNext() ?
                    MathUtil.getAverage(samplesP) : 0.0;
            double averageYaw = samplesY.iterator().hasNext() ?
                    MathUtil.getAverage(samplesY) : 0.0;

            if ((MathUtil.isNearlySame(averagePitch, lastAveragePitch, 1.0E-4) ||
                    MathUtil.isNearlySame(averageYaw, lastAverageYaw, 1.0E-4))) {
                if (++violations > 5.0) {
                    fail("* Consistent changes\n §f* avgPitch: §b" + averagePitch +
                            "\n §f* avgYaw: §b" + averageYaw, getBanVL(), 300L);
                }
            } else {
                violations = Math.max(violations - 1.25, 0.0);
            }

            samplesP.clear();
            samplesY.clear();
            lastAverageYaw = averageYaw;
            lastAveragePitch = averagePitch;
        }
    }
}