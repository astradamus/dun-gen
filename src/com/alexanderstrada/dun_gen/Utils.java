package com.alexanderstrada.dun_gen;

import com.alexanderstrada.dun_gen.tile_map.Direction;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static Map<Integer, List<Integer>> getRegions(int[] tiles) {

        final Map<Integer, List<Integer>> out = new HashMap<>();

        for (int i = 0; i < tiles.length; i++) {
            int identity = tiles[i];
            if (identity < 0) continue;

            if (!out.containsKey(identity)) {
                out.put(identity, new ArrayList<>());
            }

            out.get(identity).add(i);
        }

        return out;
    }

    public static List<Integer> getOpenNeighbors(TileMap tileMap, int origin, List<Direction> directions) {
        List<Integer> out = new ArrayList<>();
        List<Integer> openNeighbors = getMatchingNeighbors(tileMap, origin, directions, TileMap.TILE_WALL, false);
        for (int openNeighbor : openNeighbors) {
            out.add(openNeighbor);
        }
        return out;
    }

    public static List<Integer> getMatchingNeighbors(TileMap tileMap,
                                                     int origin,
                                                     List<Direction> directions,
                                                     int valueToMatch,
                                                     boolean matchIfEquals) {

        int w = tileMap.getWidth();
        int h = tileMap.getHeight();
        int bound = tileMap.getBoundary();
        int[] tiles = tileMap.getLayer(TileMap.Layer.TILES.id);

        List<Integer> matches = new ArrayList<>();

        for (Direction direction : directions) {

            final int neighbor = origin + direction.get2dIndexOffset(h);

            if (Utils.isInBounds(neighbor, w, h, bound)) {

                boolean matchesValue = tiles[neighbor] == valueToMatch;
                if (matchesValue == matchIfEquals) {
                    matches.add(neighbor);
                }
            }
        }
        return matches;
    }

    public static List<Integer> getOpenInRange(TileMap tileMap,
                                               int origin,
                                               int minDistance,
                                               int maxDistance) {

        int w = tileMap.getWidth();
        int h = tileMap.getHeight();
        int bound = tileMap.getBoundary();
        int[] tiles = tileMap.getLayer(TileMap.Layer.TILES.id);

        List<Integer> openInRange = new ArrayList<>();
        for (int y = -maxDistance; y <= maxDistance; y++) {
            for (int x = -maxDistance; x <= maxDistance; x++) {
                final int candidate = origin + Utils.getArrayIndex(x, y, h);

                final boolean violatesMinimumDistance = Utils.getDistance(candidate, origin, h) < minDistance;
                final boolean violatesTileMapBounds = !Utils.isInBounds(candidate, w, h, bound);

                if (violatesMinimumDistance || violatesTileMapBounds) continue;

                if (tiles[candidate] != TileMap.TILE_WALL) {
                    openInRange.add(candidate);
                }
            }
        }
        return openInRange;
    }
}
