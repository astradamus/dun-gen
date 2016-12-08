package com.alexanderstrada.dun_gen.map;

import com.alexanderstrada.dun_gen.Utils;

import java.util.Random;

public class Vector {
    private final int x;
    private final int y;

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector offsetBy(Direction direction) {
        return offsetBy(direction.offX, direction.offY);
    }

    public Vector offsetBy(int oX, int oY) {
        return new Vector(getX() + oX, getY() + oY);
    }

    public int distanceTo(Vector other) { return Utils.getDistance(x, y, other.x, other.y); }

    public int toArrayIndex(int mapHeight) {
        return Utils.getArrayIndex(getX(), getY(), mapHeight);
    }

    public boolean isEqualTo(Vector other) {
        return other.getX() == getX() && other.getY() == getY();
    }

    public boolean isCardinalAdjacent(Vector other) {
        int dX = Math.abs(other.getX() - getX());
        int dY = Math.abs(other.getY() - getY());

        return (dX + dY == 1);
    }

    public boolean isInBounds(int width, int height, int edgeBoundThickness) {
        boolean xOk = getX() >= edgeBoundThickness && getX() < width - edgeBoundThickness;
        boolean yOk = getY() >= edgeBoundThickness && getY() < height - edgeBoundThickness;
        return xOk && yOk;
    }

    public static Vector getRandom(Random random, int xMin, int xMax, int yMin, int yMax) {
        return new Vector(Utils.randomIntInRange(random, xMin, xMax),
                Utils.randomIntInRange(random, yMin, yMax));
    }
}
