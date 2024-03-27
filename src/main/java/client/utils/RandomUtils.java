package client.utils;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    public static final SecureRandom RANDOM = new SecureRandom();
    public static ThreadLocalRandom random = ThreadLocalRandom.current();

    public static boolean percent(int percent) {
        int random = nextInt(0, 100);
        return (percent >= random);
    }

    public static int nextInt(int min, int max) {
        int range = max - min;
        int result = min + random.nextInt(range + 1);
        return result;
    }
    public static double calculateTime(double mincps, double maxcps) {
        int cps;
        if (mincps > maxcps)
            mincps = maxcps;
        cps = (client.utils.RandomUtils.nextInt((int) mincps, (int) maxcps) + client.utils.RandomUtils.nextInt(-3,3));
        if (cps > maxcps)
            cps = (int)maxcps;

        return   ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);

    }


    public static double nextDouble(double startInclusive, double endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0D)
            return startInclusive;
        return startInclusive + (endInclusive - startInclusive) * Math.random();
    }

    public static float nextFloat(float startInclusive, float endInclusive) {
        if (startInclusive == endInclusive || endInclusive - startInclusive <= 0.0F)
            return startInclusive;
        return (float)(startInclusive + (endInclusive - startInclusive) * Math.random());
    }
}
