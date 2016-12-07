package com.alexanderstrada.dun_gen.map;

import java.util.ArrayList;
import java.util.List;

public class Map {

    public static final int HIGHLIGHT_TILE = -0xFF0000;
    public static final int WORKING_TILE = -0xFFFFFF;
    public static final int FINISHED_TILE = -0x333333;
    public static final int WALL_TILE = 0x000000;

    private final int width;
    private final int height;
    private final int[] tiles;

    public Map(int width, int height, int[] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getTiles() {
        return tiles;
    }

    public static List<Vector> getOpenNeighbors(int width,
                                                int height,
                                                int[] tiles,
                                                Vector origin,
                                                List<Direction> directions) {

        List<Vector> openNeighbors = new ArrayList<>();
        for (Direction direction : directions) {
            final Vector neighbor = origin.offsetBy(direction);
            if (neighbor.isInBounds(width, height, 1) && tiles[neighbor.toArrayIndex(height)] != WALL_TILE) {
                openNeighbors.add(neighbor);
            }
        }
        return openNeighbors;
    }
}
