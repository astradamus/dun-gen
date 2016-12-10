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

    public java.util.Map<Integer, List<Integer>> getRegions() {

        final java.util.Map<Integer, List<Integer>> out = new HashMap<>();

        for (int i = 0; i < tiles.length; i++) {
            int identity = tiles[i];
            if (identity <= 0) continue;

            if (!out.containsKey(identity)) {
                out.put(identity, new ArrayList<>());
            }

            out.get(identity).add(i);
        }

        return out;
    }

    public List<Integer> getOpenNeighbors(int origin2d, List<Direction> directions) {
        List <Integer> out = new ArrayList<>();
        List<Integer> openNeighbors = getMatchingNeighbors(origin2d, directions, Map.WALL_TILE, false);
        for (int openNeighbor : openNeighbors) {
            out.add(openNeighbor);
        }
        return out;
    }

    public List<Integer> getMatchingNeighbors(int origin,
                                              List<Direction> directions,
                                              int valueToMatch,
                                              boolean matchIfEquals) {

        List<Integer> matches = new ArrayList<>();
        for (Direction direction : directions) {
            final int neighbor = origin + (direction.get2dIndexOffset(height));
            if (Utils.isInBounds(neighbor, width, height, boundary)) {

                boolean matchesValue = tiles[neighbor] == valueToMatch;
                if (matchesValue == matchIfEquals) {
                    matches.add(neighbor);
                }
            }
        }
        return matches;
    }

    /**
     * Returns a list of all in-bounds indices in the given range that satisfy the given predicate. The predicate is
     * defined as follows: if {@code matchIfEquals} is {@code true}, then an index is included if the tile it represents
     * is equal to {@code valueToMatch}; however, if {@code matchIfEquals} is {@code false}, then an index is included
     * if the tile it represents is NOT equal to {@code valueToMatch}.
     */
    public List<Integer> getMatchingOpenInRange(int origin,
                                                int minDistance,
                                                int maxDistance,
                                                int valueToMatch,
                                                boolean matchIfEquals) {

        List<Integer> matchesInRange = new ArrayList<>();
        for (int y = -maxDistance; y <= maxDistance; y++) {
            for (int x = -maxDistance; x <= maxDistance; x++) {
                final int candidate = origin + Utils.getArrayIndex(x, y, height);
                if (Utils.getDistance(candidate, origin, height) < minDistance) continue;

                if (Utils.isInBounds(candidate, width, height, boundary)) {

                    int tileValue = tiles[candidate];
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
