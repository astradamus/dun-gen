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
    private final int boundary;
    private final int[] tiles;

    public Map(int width, int height, int boundary, int[] tiles) {
        this.width = width;
        this.height = height;
        this.boundary = boundary;
        this.tiles = tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBoundary() {
        return boundary;
    }

    public int[] getTiles() {
        return tiles;
    }

    public List<Vector> getOpenNeighbors(Vector origin, List<Direction> directions) {
        return getMatchingNeighbors(origin, directions, Map.WALL_TILE, false);
    }

    public List<Vector> getMatchingNeighbors(Vector origin,
                                             List<Direction> directions,
                                             int valueToMatch,
                                             boolean matchIfEquals) {

        List<Vector> matches = new ArrayList<>();
        for (Direction direction : directions) {
            final Vector neighbor = origin.offsetBy(direction);
            if (neighbor.isInBounds(width, height, boundary)) {

                boolean matchesValue = tiles[neighbor.toArrayIndex(height)] == valueToMatch;
                if (matchesValue == matchIfEquals) {
                    matches.add(neighbor);
                }
            }
        }
        return matches;
    }
}
