package com.alexanderstrada.dun_gen;

import java.util.Random;

public class Utils {

    public static int randomIntInRange(Random random, int min, int max) {
        return min + random.nextInt(max-min);
    }

    public static int getArrayIndex(int x, int y, int height2d) {
        return x * height2d + y;
    }
}
