package com.alexanderstrada.dun_gen.gen.maze;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;
import com.alexanderstrada.dun_gen.map.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GrowingTreeMaze extends BasicGenerator {

    private double turningChance;
    private int minimumSpacing;
    private int extraBoundary;

    private final List<Vector> working = new ArrayList<>();
    private int startPointScanX = 0;
    private int startPointScanY = 0;
    private Vector highlight;
    private Direction direction = null;

    public GrowingTreeMaze(Random random) {
        super(random);
    }

    public GrowingTreeMaze(Random random, double turningChance, int minimumSpacing, int extraBoundary) {
        super(random);
        setTurningChance(turningChance);
        setMinimumSpacing(minimumSpacing);
        setExtraBoundary(extraBoundary);
    }

    public double getTurningChance() {
        return turningChance;
    }

    public void setTurningChance(double tc) {
        turningChance = tc;
    }

    public int getMinimumSpacing() {
        return minimumSpacing;
    }

    public void setMinimumSpacing(int ms) {
        minimumSpacing = Math.max(2, ms);
    }

    public int getExtraBoundary() {
        return extraBoundary;
    }

    public void setExtraBoundary(int bm) {
        extraBoundary = Math.max(0, bm);
    }

    @Override
    public void apply(Map map, long updateDelay) {
        super.apply(map, updateDelay);
        boundary += extraBoundary;

        do {
            carveMaze(updateDelay);
            Utils.maybeWait(this, updateDelay);
            selectStartingPoint();
        } while (!working.isEmpty());

        finalizeTiles();
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
            notifyGenerationListener();
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
                direction = null;
                working.remove(origin);
                tiles[origin.toArrayIndex(height)] = Map.WORKED_TILE;
            }

            notifyGenerationListener();
        }
    }

    private Vector getCarveOrigin() {
        return working.get(working.size() - 1);
    }

    private Vector selectCarveTarget(Vector origin) {

        // Check each cardinal neighbor (in random order) until a suitable target is found or all are checked.
        List<Direction> cardinals = Direction.getCardinals();
        while (!cardinals.isEmpty()) {

            // When carving continuously, only turn according to turningChance.
            if (direction == null || random.nextDouble() < turningChance) {
                direction = cardinals.remove(random.nextInt(cardinals.size()));
            }

            // Select and validate the target chosen. If it's invalid, skip it.
            Vector possibleTarget = origin.offsetBy(direction);
            if (!isTargetValid(origin, possibleTarget)) {
                direction = null;
                continue;
            }

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

        if (direction != null) {
            if (testForwardForSelf(target.toArrayIndex(height))) return false;
        }
        else {
            List<Vector> openNearby = map.getMatchingOpenInRange(target, 2, minimumSpacing, Map.FINISHED_TILE, false);
            if (openNearby.size() > 0) {
                return false;
            }
        }

        List<Vector> openCards = map.getOpenNeighbors(target, Direction.getCardinals());
        List<Vector> openDiags = map.getOpenNeighbors(target, Direction.getDiagonals());

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

    private boolean testForwardForSelf(int target) {

        Direction left = direction.leftCardinal();
        Direction right = direction.rightCardinal();

        int forwardOffI = direction.get2dIndexOffset(height);
        int leftOffI = left.get2dIndexOffset(height);
        int rightOffI = right.get2dIndexOffset(height);

        for (int i = 2; i <= minimumSpacing; i++) {

            int forward = target + (forwardOffI * i);
            if (testIndexIsSelf(forward)) return true;

            int forward_left = forward + leftOffI;
            if (testIndexIsSelf(forward_left)) return true;

            int forward_right = forward + rightOffI;
            if (testIndexIsSelf(forward_right)) return true;

            int leftward = target + (leftOffI * i);
            if (testIndexIsSelf(leftward)) return true;

            int rightward = target + (rightOffI * i);
            if (testIndexIsSelf(rightward)) return true;
        }

        return false;
    }

    private boolean testIndexIsSelf(int i) {
        if (Utils.isInBounds(i, width, height, boundary)) {
            int testValue = tiles[i];
            if (testValue == Map.WORKED_TILE || testValue == Map.WORKING_TILE) {
                return true;
            }
        }
        return false;
    }

    private void finalizeTiles() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == Map.WORKED_TILE) {
                tiles[i] = Map.FINISHED_TILE;
            }
        }
        notifyGenerationListener();
    }
}
