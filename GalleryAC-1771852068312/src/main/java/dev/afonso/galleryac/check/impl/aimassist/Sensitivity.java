package dev.afonso.galleryac.check.impl.aimassist;

import dev.afonso.galleryac.GalleryAC;
import dev.afonso.galleryac.check.AbstractCheck;
import dev.afonso.galleryac.check.CheckType;
import dev.afonso.galleryac.data.MovementData;
import dev.afonso.galleryac.data.PlayerData;
import dev.afonso.galleryac.util.EvictingList;
import dev.afonso.galleryac.util.MathUtil;

public class Sensitivity extends AbstractCheck {

    private final EvictingList<Float> pitchGcdList = new EvictingList<>(50);
    private final EvictingList<Float> pitchGcdList2 = new EvictingList<>(50);
    private float lastDeltaPitch;
    public float pitchMode;
    public double sensPercent;
    public float sensitivityY;

    public Sensitivity(GalleryAC plugin, PlayerData playerData) {
        super(plugin, playerData, CheckType.SENSITIVITY);
    }

    @Override
    public void handle() {
        MovementData data = playerData.getMovementData();

        if (!enabled) return;
        if (data.isTeleporting()) return;

        float deltaPitch = data.getDeltaPitch();

        if (deltaPitch >= 4.0f) return;

        float pitchGcd = MathUtil.getGcd(deltaPitch, lastDeltaPitch);

        if (pitchGcd > 0.009f && Math.abs(data.getPitch()) < 0.6f && Math.abs(data.getLastPitch()) < 0.6f) {
            pitchGcdList.add(pitchGcd);
            if (pitchGcdList.size() == 5) {
                pitchMode = MathUtil.getMode(pitchGcdList);
                float test1 = convertToMouseDelta(pitchMode);
                sensPercent = Math.floor(test1 * 200.0);
                data.setSensitivity((float) sensPercent);
                data.setSensitivityY(test1);
                pitchGcdList.clear();
            }
        }

        if (pitchGcd > 0.009f) {
            pitchGcdList2.add(pitchGcd);
            if (pitchGcdList2.size() > 40) {
                pitchMode = MathUtil.getMode(pitchGcdList2);
                float test1 = convertToMouseDelta(pitchMode);
                sensPercent = Math.floor(test1 * 200.0);
                data.setSensitivity((float) sensPercent);
                data.setSmallestRotationGCD(pitchMode);
                if (pitchGcdList2.size() == 50) {
                    pitchGcdList2.clear();
                }
            }
        }

        if (pitchGcd > 0.008f) {
            data.setPitchGCD(Math.min(data.getPitchGCD(), deltaPitch));
        }

        lastDeltaPitch = deltaPitch;
    }

    private float convertToMouseDelta(float value) {
        return (float) ((Math.cbrt(value / 0.15f / 8.0f) - 0.2f) / 0.6f);
    }
}