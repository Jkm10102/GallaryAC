package dev.afonso.galleryac.util;

public class GCDUtil {

    public static double getGcd(double current, double previous) {
        if (current == 0.0 || previous == 0.0) {
            return 0.0;
        }
        return MathUtil.gcd(current, previous);
    }

    public static long getGcdLong(long current, long previous) {
        if (current == 0L || previous == 0L) {
            return 0L;
        }
        return MathUtil.gcd(current, previous);
    }

    public static double getSensitivityFromGcd(double gcd) {
        double sensitivity = gcd / 0.15;
        return Math.round(sensitivity * 100.0) / 100.0;
    }

    public static boolean isValidGcd(double gcd) {
        return gcd > 0.0 && gcd < 20.0;
    }
}