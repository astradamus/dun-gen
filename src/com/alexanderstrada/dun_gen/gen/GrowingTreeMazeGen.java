package com.alexanderstrada.dun_gen.gen;

import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;
import com.alexanderstrada.dun_gen.map.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GrowingTreeMazeGen implements com.alexanderstrada.dun_gen.gen.Generator {

    final Random random;
    final int boundary;
    final double turningResistance;
    final List<Vector> working = new ArrayList<>();

    private com.alexanderstrada.dun_gen.gen.GenerationListener listener;
    private int width;
    private int height;
    private int[] tiles;
    private Vector highlight;
    private Direction lastDirection = null;

    public GrowingTreeMazeGen(Random random, int edgeBoundary, double turningResistance) {
        this.random = random;
        this.boundary = edgeBoundary;
        this.turningResistance = turningResistance;
    }

    @Override
    public void setGenerationListener(com.alexanderstrada.dun_gen.gen.GenerationListener listener) {
        this.listener = listener;
    }

    @Override
    public void apply(Map map, long updateDelay) {
        width = map.getWidth();
        height = map.getHeight();
        tiles = map.getTiles();

        selectStartingPoint();
        carveMaze(updateDelay);
    }

    private void selectStartingPoint() {
        Vector first = null;
        while (first == null || !isTargetValid(first, first)) {
            first = Vector.getRandom(random, boundary, width - boundary, boundary, height - boundary);
        }
        carveTile(first);
        listener.notifyVisualizerMapUpdated();
    }

    private void carveTile(Vector first) {
        tiles[first.toArrayIndex(height)] = Map.HIGHLIGHT_TILE;
        working.add(first);
        highlight = first;
    }

    private void carveMaze(long updateDelay) {

        // Carve until we run out of working points.
        while (!working.isEmpty()) {

            maybeWait(updateDelay);

            // Clear highlight.
            if (highlight != null) {
                tiles[highlight.toArrayIndex(height)] = Map.WORKING_TILE;
                highlight = null;
            }

            Vector origin = getCarveOrigin();
            Vector target = getCarveTarget(origin);

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

            listener.notifyVisualizerMapUpdated();
        }
    }

    private void maybeWait(long updateDelay) {
        if (updateDelay > 0) {
            try {
                synchronized (this) {
                    wait(updateDelay);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Vector getCarveOrigin() {
        return working.get(working.size() - 1);
    }

    private Vector getCarveTarget(Vector origin) {

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

        // Don't carve tiles with too many neighbors. Prevents creation of rooms/nodes.
        if (getOpenNeighbors(Direction.getAll(), target).size() > 2) {
            return false;
        }

        // Don't carve tiles that create diagonal connections.
        for (Vector openDiag : getOpenNeighbors(Direction.getDiagonals(), target)) {
            if (!openDiag.isCardinalAdjacent(origin)) {
                return false;
            }
        }
        return true;
    }

    private List<Vector> getOpenNeighbors(List<Direction> directions,
                                          Vector possibleTarget) {

        List<Vector> openNeighbors = new ArrayList<>();
        for (Direction direction : directions) {
            final Vector neighbor = possibleTarget.offsetBy(direction);
            if (neighbor.isInBounds(width, height, 1) && tiles[neighbor.toArrayIndex(height)] != Map.WALL_TILE) {
                openNeighbors.add(neighbor);
            }
        }
        return openNeighbors;
    }
}
