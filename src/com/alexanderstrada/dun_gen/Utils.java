package com.alexanderstrada.dun_gen;

import com.alexanderstrada.dun_gen.map.Vector;

import java.util.Random;

public class Utils {

    public static int randomIntInRange(Random random, int min, int max) {
        if (min == max) return min;
        return min + random.nextInt(max-min);
    }

    public static int getArrayIndex(int x, int y, int height2d) {
        return x * height2d + y;
    }

    public static Vector getVectorFromIndex(int i, int height2d) {
        int x = i / height2d;
        int y = i % height2d;

        return new Vector(x, y);
    }

    public static void maybeWait(Object lock, long time) {
        if (time > 0) {
            try {
                synchronized (lock) {
                    lock.wait(time);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
