package com.alexanderstrada.dun_gen.tile_map;

import com.alexanderstrada.dun_gen.Utils;

import java.util.ArrayList;
import java.util.List;

public enum Direction {
    NORTH(0, +0, -1),
    NE   (1, +1, -1),
    EAST (2, +1, +0),
    SE   (3, +1, +1),
    SOUTH(4, +0, +1),
    SW   (5, -1, +1),
    WEST (6, -1, +0),
    NW   (7, -1, -1);

    public final int id;
    public final int offX;
    public final int offY;

    Direction(int id, int offX, int offY) {
        this.id = id;
        this.offX = offX;
        this.offY = offY;
    }

    public int get2dIndexOffset(int height2d) {
        return Utils.getArrayIndex(offX, offY, height2d);
    }

    public Direction left() {
        return values()[calcLeft()];
    }

    public Direction right() {
        return values()[(id+1)%values().length];
    }

    public Direction leftCardinal() {
        int i = calcLeft();
        i -= (i%2);
        return values()[i];
    }

    public Direction rightCardinal() {
        int i = (id+1);
        i += (i%2);
        i %= values().length;
        return values()[i];
    }

    private int calcLeft() {
        return (id - 1 + values().length) % values().length;
    }

    public static List<Direction> getCardinals() {
        List<Direction> list = new ArrayList<>();
        list.add(NORTH);
        list.add(EAST);
        list.add(SOUTH);
        list.add(WEST);
        return list;
    }

    public static List<Direction> getDiagonals() {
        List<Direction> list = new ArrayList<>();
        list.add(NW);
        list.add(NE);
        list.add(SW);
        list.add(SE);
        return list;
    }

    public static List<Direction> getAll() {
        List<Direction> list = new ArrayList<>();
        list.add(NORTH);
        list.add(EAST);
        list.add(SOUTH);
        list.add(WEST);
        list.add(NW);
        list.add(NE);
        list.add(SW);
        list.add(SE);
        return list;
    }
}
