package com.alexanderstrada.dun_gen.gen;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;
import com.alexanderstrada.dun_gen.map.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GrowingTreeMaze implements Generator {

    final Random random;
    final int boundary;
    final double turningResistance;
    final List<Vector> working = new ArrayList<>();

    private boolean preserveHooks = false;

    private GenerationListener listener;
    private int width;
    private int height;
    private int[] tiles;

    private int startPointScanX = 0;
    private int startPointScanY = 0;

    private Vector highlight;
    private Direction lastDirection = null;

    public GrowingTreeMaze(Random random, int edgeBoundary, double turningResistance) {
        this.random = random;
        this.boundary = edgeBoundary;
        this.turningResistance = turningResistance;
    }

    @Override
    public void setGenerationListener(GenerationListener listener) {
        this.listener = listener;
    }

    @Override
    public void apply(Map map, long updateDelay) {
        width = map.getWidth();
        height = map.getHeight();
        tiles = map.getTiles();

        do {
            carveMaze(updateDelay);
            Utils.maybeWait(this, updateDelay);
            selectStartingPoint();
        } while (!working.isEmpty());

        if (!preserveHooks) {
            cullHooks(updateDelay);
        }
    }

    public GrowingTreeMaze setPreserveHooks(boolean preserveHooks) {
        this.preserveHooks = preserveHooks;
        return this;
    }

    private void notifyListener() {
        if (listener != null) listener.notifyVisualizerMapUpdated();
    }

    private void cullHooks(long updateDelay) {

        List<Vector> hooks = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Vector v = new Vector(x, y);
                boolean tileIsOpen = tiles[v.toArrayIndex(height)] != Map.WALL_TILE;

                if (tileIsOpen) {
                    List<Vector> openCards = Map.getOpenNeighbors(width, height, tiles, v, Direction.getCardinals());
                    List<Vector> openDiags = Map.getOpenNeighbors(width, height, tiles, v, Direction.getDiagonals());

                    boolean isHook = (openCards.size() == 1 && openDiags.size() == 1);

                    if (isHook) {
                        hooks.add(v);
                    }
                }
            }
        }

        for (Vector hook : hooks) {
            tiles[hook.toArrayIndex(height)] = Map.WALL_TILE;
            Utils.maybeWait(this, updateDelay);
            notifyListener();
        }
    }

    private void selectStartingPoint() {
        Vector first = null;
        xy: for (; startPointScanY < height; startPointScanY++) {
            for (; startPointScanX < width; startPointScanX++) {
                Vector candidate = new Vector(startPointScanX, startPointScanY);
                if (isTargetValid(null, candidate)) {
                    first = candidate;
                    break xy;
                }
            }
            startPointScanX = 0;
        }
        if (first != null) {
            carveTile(first);
            notifyListener();
        }
    }

    private void carveTile(Vector first) {
        tiles[first.toArrayIndex(height)] = Map.HIGHLIGHT_TILE;
        working.add(first);
        highlight = first;
    }

    private void carveMaze(long updateDelay) {

        // Carve until we run out of working points.
        while (!working.isEmpty()) {

            Utils.maybeWait(this, updateDelay);

            // Clear highlight.
            if (highlight != null) {
                tiles[highlight.toArrayIndex(height)] = Map.WORKING_TILE;
                highlight = null;
            }

            Vector origin = getCarveOrigin();
            Vector target = selectCarveTarget(origin);

            // If we found a suitable target, carve it.
            if (target != null) {
                carveTile(target);
            }

            // If not, 'origin' is a dead end. Remove it from the working list.
            else {
                lastDirection = null;
                working.remove(origin);
                tiles[origin.toArrayIndex(height)] = Map.FINISHED_TILE;
            }

            notifyListener();
        }
    }

    private Vector getCarveOrigin() {
        return working.get(working.size() - 1);
    }

    private Vector selectCarveTarget(Vector origin) {

        // Check each cardinal neighbor (in random order) until a suitable target is found or all are checked.
        List<Direction> cardinals = Direction.getCardinals();
        while (!cardinals.isEmpty()) {

            Direction chosen;

            // When carving continuously, avoid turning according to turningResistance.
            if (lastDirection != null && random.nextDouble() < turningResistance) {
                chosen = lastDirection;
                lastDirection = null;
            } else {
                chosen = cardinals.remove(random.nextInt(cardinals.size()));
            }

            // Select and validate the target chosen. If it's invalid, skip it.
            Vector possibleTarget = origin.offsetBy(chosen);
            if (!isTargetValid(origin, possibleTarget)) continue;

            lastDirection = chosen;
            return possibleTarget;
        }
        return null;
    }

    private boolean isTargetValid(Vector origin, Vector target) {

        // Don't carve outside of allowed bounds.
        if (!target.isInBounds(width, height, boundary)) {
            return false;
        }

        // Don't carve tiles that are already open.
        if (tiles[target.toArrayIndex(height)] != Map.WALL_TILE) {
            return false;
        }

        List<Vector> openCards = Map.getOpenNeighbors(width, height, tiles, target, Direction.getCardinals());
        List<Vector> openDiags = Map.getOpenNeighbors(width, height, tiles, target, Direction.getDiagonals());

        int oc = openCards.size();
        int od = openDiags.size();

        boolean isIsolate = (oc == 0 && od == 0);
        boolean isDeadEnd = (oc == 1 && od <= 1); // The <=1 instead of ==0 allows turning of corners and T-junctions,
                                                  // but also results in most halls ending with little 'hooks'.

        if (origin == null && !isIsolate) {
            return false;
        }

        if (!isIsolate && !isDeadEnd) {
            return false;
        }

        // Ensure we're not creating diagonal connections to other open spaces.
        for (Vector openDiag : openDiags) {
            if (!openDiag.isCardinalAdjacent(origin)) {
                return false;
            }
        }
        return true;
    }
}