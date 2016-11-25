package com.alexanderstrada.dun_gen.map;

import java.util.ArrayList;
import java.util.List;

public enum Direction {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0),

    NW(-1, -1),
    NE(1, -1),
    SW(-1, 1),
    SE(1, 1);

    public final int offX;
    public final int offY;

    Direction(int offX, int offY) {
        this.offX = offX;
        this.offY = offY;
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
