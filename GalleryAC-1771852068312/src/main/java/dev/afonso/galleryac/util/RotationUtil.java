package dev.afonso.galleryac.util;

public class RotationUtil {

    public static double getAngleDelta(double from, double to) {
        double delta = Math.abs(from - to) % 360.0;
        if (delta > 180.0) {
            delta = 360.0 - delta;
        }
        return delta;
    }

    public static double getYawChange(float from, float to) {
        double delta = to - from;
        if (delta < -180.0) delta += 360.0;
        if (delta > 180.0) delta -= 360.0;
        return Math.abs(delta);
    }

    public static double getPitchChange(float from, float to) {
        return Math.abs(to - from);
    }

    public static boolean isValidYaw(float yaw) {
        return !Float.isNaN(yaw) && !Float.isInfinite(yaw);
    }

    public static boolean isValidPitch(float pitch) {
        return !Float.isNaN(pitch) && !Float.isInfinite(pitch) && pitch >= -90.0f && pitch <= 90.0f;
    }

    public static double wrapDegrees(double degrees) {
        double wrapped = degrees % 360.0;
        if (wrapped >= 180.0) {
            wrapped -= 360.0;
        }
        if (wrapped < -180.0) {
            wrapped += 360.0;
        }
        return wrapped;
    }
}