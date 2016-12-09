package com.alexanderstrada.dun_gen.map;

import com.alexanderstrada.dun_gen.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {

    public static final int HIGHLIGHT_TILE = -0xFF0000;
    public static final int WORKING_TILE = -0xFFFFFF;
    public static final int WORKED_TILE = -0x666666;
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

    public java.util.Map<Integer, List<Vector>> getRegions() {

        final java.util.Map<Integer, List<Vector>> out = new HashMap<>();

        for (int i = 0; i < tiles.length; i++) {
            int identity = tiles[i];
            if (identity <= 0) continue;

            if (!out.containsKey(identity)) {
                out.put(identity, new ArrayList<>());
            }

            out.get(identity).add(Utils.getVectorFromIndex(i, height));
        }

        return out;
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

    /**
     * Returns a list of all in-bounds vectors in the given range that satisfy the given predicate. The predicate is
     * defined as follows: if {@code matchIfEquals} is {@code true}, then a vector is included if the tile it represents
     * is equal to {@code valueToMatch}; however, if {@code matchIfEquals} is {@code false}, then a vector is included
     * if the tile it represents is NOT equal to {@code valueToMatch}.
     */
    public List<Vector> getMatchingOpenInRange(Vector origin,
                                               int minDistance,
                                               int maxDistance,
                                               int valueToMatch,
                                               boolean matchIfEquals) {

        List<Vector> matchesInRange = new ArrayList<>();
        for (int y = -maxDistance; y <= maxDistance; y++) {
            for (int x = -maxDistance; x <= maxDistance; x++) {
                final Vector candidate = origin.offsetBy(x, y);
                if (candidate.distanceTo(origin) < minDistance) continue;

                if (candidate.isInBounds(width, height, boundary)) {

                    int tileValue = tiles[candidate.toArrayIndex(height)];
                    if (tileValue != Map.WALL_TILE) {

                        boolean matchesValue = tileValue == valueToMatch;
                        if (matchesValue == matchIfEquals) {
                            matchesInRange.add(candidate);
                        }
                    }
                }
            }
        }
        return matchesInRange;
    }
}
