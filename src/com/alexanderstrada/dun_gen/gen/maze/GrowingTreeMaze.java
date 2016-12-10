package com.alexanderstrada.dun_gen.gen.maze;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.tile_map.Direction;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GrowingTreeMaze extends BasicGenerator {

    private double turningChance;
    private int minimumSpacing;
    private int extraBoundary;

    private final List<Integer> working = new ArrayList<>();
    private int startPointScan = 0;
    private int highlightIndex;
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
    public void apply(TileMap tileMap, long updateDelay) {
        super.apply(tileMap, updateDelay);
        boundary += extraBoundary;

        do {
            carveMaze(updateDelay);
            Utils.maybeWait(this, updateDelay);
            selectStartingPoint();
        } while (!working.isEmpty());

        finalizeTiles();
    }

    private void selectStartingPoint() {
        int first = -1;
        for (; startPointScan < tiles.length; startPointScan++) {
            if (isTargetValid(-1, startPointScan)) {
                first = startPointScan;
                break;
            }
        }

        if (first >= 0) {
            carveTile(first);
            notifyGenerationListener();
        }
    }

    private void carveTile(int targetIndex) {
        tiles[targetIndex] = TileMap.HIGHLIGHT_TILE;
        working.add(targetIndex);
        highlightIndex = targetIndex;
    }

    private void carveMaze(long updateDelay) {

        // Carve until we run out of working points.
        while (!working.isEmpty()) {

            Utils.maybeWait(this, updateDelay);

            // Clear highlightIndex.
            if (highlightIndex >= 0) {
                tiles[highlightIndex] = TileMap.WORKING_TILE;
                highlightIndex = -1;
            }

            Integer origin = working.get(working.size() - 1);
            int target = selectCarveTarget(origin);

            // If we found a suitable target, carve it.
            if (target >= 0) {
                carveTile(target);
            }

            // If not, 'origin' is a dead end. Remove it from the working list.
            else {
                direction = null;
                working.remove(origin);
                tiles[origin] = TileMap.WORKED_TILE;
            }

            notifyGenerationListener();
        }
    }

    private int selectCarveTarget(int origin) {

        // Check each cardinal neighbor (in random order) until a suitable target is found or all are checked.
        List<Direction> cardinals = Direction.getCardinals();
        while (!cardinals.isEmpty()) {

            // When carving continuously, only turn according to turningChance.
            if (direction == null || random.nextDouble() < turningChance) {
                direction = cardinals.remove(random.nextInt(cardinals.size()));
            }

            // Select and validate the target chosen. If it's invalid, skip it.
            int possibleTarget = origin + direction.get2dIndexOffset(height);
            if (!isTargetValid(origin, possibleTarget)) {
                direction = null;
                continue;
            }

            return possibleTarget;
        }
        return -1;
    }

    private boolean isTargetValid(int origin, int target) {

        // Don't carve outside of allowed bounds.
        if (!Utils.isInBounds(target, width, height, boundary)) {
            return false;
        }

        // Don't carve tiles that are already open.
        if (tiles[target] != TileMap.WALL_TILE) {
            return false;
        }

        if (direction != null) {
            if (testForwardForSelf(target)) {
                return false;
            }
        }
        else {
            List<Integer> openNearby =
                    Utils.getMatchingOpenInRange(tileMap, target, 2, minimumSpacing, TileMap.FINISHED_TILE, false);
            if (openNearby.size() > 1) {
                return false;
            }
        }

        List<Integer> openCards = Utils.getOpenNeighbors(tileMap, target, Direction.getCardinals());
        List<Integer> openDiags = Utils.getOpenNeighbors(tileMap, target, Direction.getDiagonals());

        int oc = openCards.size();
        int od = openDiags.size();

        boolean isIsolate = (oc == 0 && od == 0);
        boolean isDeadEnd = (oc == 1 && od <= 1); // The <=1 instead of ==0 allows turning of corners and T-junctions,
                                                  // but also results in most halls ending with little 'hooks'.

        if (origin < 0 && !isIsolate) {
            return false;
        }

        if (!isIsolate && !isDeadEnd) {
            return false;
        }

        // Ensure we're not creating diagonal connections to other open spaces.
        for (int openDiag : openDiags) {
            if (!Utils.isCardinalAdjacent(openDiag, origin, height)) {
                return false;
            }
        }
        return true;
    }

    private boolean testForwardForSelf(int target) {

        int scanOrigin;
        int scanWidth, scanHeight;
        int xOff, yOff;
        int ms = minimumSpacing;

        switch (direction) {
            case NORTH:
                xOff = -ms;
                yOff = -ms;
                scanWidth = ms * 2;
                scanHeight = ms;
                break;
            case EAST:
                xOff = 0;
                yOff = -ms;
                scanWidth = ms;
                scanHeight = ms * 2;
                break;
            case SOUTH:
                xOff = -ms;
                yOff = 0;
                scanWidth = ms * 2;
                scanHeight = ms;
                break;
            case WEST:
                xOff = -ms;
                yOff = -ms;
                scanWidth = ms;
                scanHeight = ms * 2;
                break;
            default:
                throw new IllegalStateException();
        }

        scanOrigin = target + Utils.getArrayIndex(xOff, yOff, height);

        for (int y = 0; y < scanHeight; y++) {
            for (int x = 0; x < scanWidth; x++) {
                int off = Utils.getArrayIndex(x, y, height);
                if (testIndexIsSelf(scanOrigin + off)) return true;
            }
        }

        return false;
    }

    private boolean testIndexIsSelf(int i) {
        if (Utils.isInBounds(i, width, height, boundary)) {
            int testValue = tiles[i];
            if (testValue == TileMap.WORKED_TILE || testValue == TileMap.WORKING_TILE) {
                return true;
            }
        }
        return false;
    }

    private void finalizeTiles() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == TileMap.WORKED_TILE) {
                tiles[i] = TileMap.FINISHED_TILE;
            }
        }
        notifyGenerationListener();
    }
}
