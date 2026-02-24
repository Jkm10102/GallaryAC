package dev.afonso.galleryac.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MathUtil {

    public static double gcd(double a, double b) {
        if (a < b) {
            return gcd(b, a);
        }
        if (Math.abs(b) < 0.001) {
            return a;
        }
        return gcd(b, a - Math.floor(a / b) * b);
    }

    public static float getGcd(float a, float b) {
        if (a < b) return getGcd(b, a);
        if (Math.abs(b) < 0.001f) return a;
        return getGcd(b, a - (float) Math.floor(a / b) * b);
    }

    public static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public static boolean isNearlySame(double d1, double d2, double threshold) {
        return Math.abs(d1 - d2) < threshold && Math.abs(d1 - d2) > 0.0;
    }

    public static double getVariance(Collection<? extends Number> data) {
        int count = 0;
        double sum = 0.0;

        for (Number value : data) {
            sum += value.doubleValue();
            count++;
        }

        if (count == 0) return 0.0;

        double variance = 0.0;
        double average = sum / count;

        for (Number value : data) {
            variance += Math.pow(value.doubleValue() - average, 2.0);
        }

        return variance / count;
    }

    public static double getAverage(Collection<? extends Number> data) {
        double sum = 0.0;
        int count = 0;

        for (Number value : data) {
            sum += value.doubleValue();
            count++;
        }

        return count == 0 ? 0.0 : sum / count;
    }

    public static double getStandardDeviation(Collection<? extends Number> data) {
        return Math.sqrt(getVariance(data));
    }

    public static <T> T getMode(Iterable<T> data) {
        Map<T, Integer> frequency = new HashMap<>();

        for (T value : data) {
            frequency.put(value, frequency.getOrDefault(value, 0) + 1);
        }

        T mode = null;
        int maxFreq = 0;

        for (Map.Entry<T, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() > maxFreq) {
                maxFreq = entry.getValue();
                mode = entry.getKey();
            }
        }

        return mode;
    }

    public static double lowest(Collection<? extends Number> data) {
        double min = Double.MAX_VALUE;
        for (Number value : data) {
            if (value.doubleValue() < min) {
                min = value.doubleValue();
            }
        }
        return min;
    }

    public static double highest(Collection<? extends Number> data) {
        double max = -Double.MAX_VALUE;
        for (Number value : data) {
            if (value.doubleValue() > max) {
                max = value.doubleValue();
            }
        }
        return max;
    }
}