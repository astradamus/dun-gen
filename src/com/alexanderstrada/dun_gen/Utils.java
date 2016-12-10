package com.alexanderstrada.dun_gen;

import com.alexanderstrada.dun_gen.map.Vector;

import java.util.Random;

public class Utils {

    public static int getDistance(int i1, int i2, int height2d) {
        int x1 = calcX(i1, height2d);
        int y1 = calcY(i1, height2d);
        int x2 = calcX(i2, height2d);
        int y2 = calcY(i2, height2d);
        return getDistance(x1, y1, x2, y2);
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);

        return Math.max(dX, dY);
    }

    public static int randomIntInRange(Random random, int min, int max) {
        if (min == max) return min;
        return min + random.nextInt(max-min);
    }

    public static int getArrayIndex(int x, int y, int height2d) {
        return x * height2d + y;
    }

    public static Vector getVectorFromIndex(int i, int height2d) {
        int x = calcX(i, height2d);
        int y = calcY(i, height2d);

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

    public static boolean isInBounds(int x, int y, int width, int height, int edgeBoundThickness) {
        boolean xOk = x >= edgeBoundThickness && x < width - edgeBoundThickness;
        boolean yOk = y >= edgeBoundThickness && y < height - edgeBoundThickness;
        return xOk && yOk;
    }

    public static boolean isInBounds(int i2d, int width, int height, int edgeBoundThickness) {
        int x = calcX(i2d, height);
        int y = calcY(i2d, height);
        return isInBounds(x, y, width, height, edgeBoundThickness);
    }

    public static boolean isCardinalAdjacent(int xy1, int xy2, int height) {
        int x1 = calcX(xy1, height);
        int y1 = calcY(xy1, height);
        int x2 = calcX(xy2, height);
        int y2 = calcY(xy2, height);
        return isCardinalAdjacent(x1, y1, x2, y2);
    }

    public static boolean isCardinalAdjacent(int x1, int y1, int x2, int y2) {
        int dX = Math.abs(x2 - x1);
        int dY = Math.abs(y2 - y1);

        return (dX + dY == 1);
    }

    public static int calcX(int i, int height2d) {
        return i / height2d;
    }

    public static int calcY(int i, int height2d) {
        return i % height2d;
    }
}
