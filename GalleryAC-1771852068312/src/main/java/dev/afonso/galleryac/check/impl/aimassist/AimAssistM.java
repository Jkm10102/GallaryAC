package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.util.EvictingList;
import dev.afonso.galleryac.util.MathUtil;

public class AimAssistM extends AbstractCheck {

    private final EvictingList<Float> pitchList = new EvictingList<>(200);

    public AimAssistM(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.AIM_ASSIST_M);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.getDeltaYaw() <= 2.5f) return;
        if (Math.abs(data.getPitch()) > 80.0f) return;
        if (data.isCinematic()) return;
        if (data.getAttackTicks() > 3) return;
        if (data.getSensitivity() == -1) return;

        pitchList.add(data.getDeltaPitch());

        if (pitchList.size() == 200) {
            double min = MathUtil.lowest(pitchList);
            double max = MathUtil.highest(pitchList);
            double difference = Math.abs(max - min);

            if (difference < data.getPitchGCD() * 1.25) {
                fail("* Weird change\n §f* d: §b" + format(4, difference) +
                        "\n §f* e: §b" + format(4, data.getPitchGCD() * 1.5),
                        getBanVL(), 300L);
            }

            pitchList.clear();
        }
    }
}