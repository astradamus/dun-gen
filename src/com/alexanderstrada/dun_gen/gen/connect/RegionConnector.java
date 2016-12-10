package com.alexanderstrada.dun_gen.gen.connect;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.process.RegionColorizer;
import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RegionConnector extends RegionColorizer {

    private final java.util.Map<Integer, List<Integer>> regions = new HashMap<>();

    public RegionConnector(Random random) {
        super(random);
    }

    @Override
    public void apply(Map map, long updateDelay) {
        super.apply(map, updateDelay);

        regions.clear();
        regions.putAll(map.getRegions());

        int repeats = 0;
        int lastSize = -1;
        while (repeats < 10) {
            if (regions.size() == lastSize) {
                repeats++;
            }
            else {
                repeats = 0;
                lastSize = regions.size();
            }
            placeConnections(updateDelay);
        }
    }

    private void placeConnections(long updateDelay) {
        for (int i = 0; i < tiles.length; i++) {

            if (tiles[i] == Map.WALL_TILE) {

                List<Integer> cardinalNeighbors = map.getOpenNeighbors(i, Direction.getCardinals());

                if (cardinalNeighbors.size() == 2) {
                    int neighbor1 = cardinalNeighbors.get(0);
                    int n1x = Utils.calcX(neighbor1, height);
                    int n1y = Utils.calcY(neighbor1, height);

                    int neighbor2 = cardinalNeighbors.get(1);
                    int n2x = Utils.calcX(neighbor2, height);
                    int n2y = Utils.calcY(neighbor2, height);

                    // Only make connections at 180 degrees.
                    if (n1x == n2x || n1y == n2y) {

                        int neighbor1Id = tiles[neighbor1];
                        int neighbor2Id = tiles[neighbor2];

                        if (neighbor1Id != neighbor2Id && random.nextDouble() < 0.10) {
                            mergeRegions(i, neighbor1Id, neighbor2Id, updateDelay);
                        }
                    }
                }
            }
        }
    }

    private void mergeRegions(int connectionIndex, int keepRegionId, int consumeRegionId, long updateDelay) {

        List<Integer> keepMembers = regions.get(keepRegionId);

        // Add the connection to the kept region.
        tiles[connectionIndex] = keepRegionId;
        keepMembers.add(connectionIndex);

        // Add all consumed members to the kept region, update them on the map.
        for (int consumeMember : regions.get(consumeRegionId)) {
            tiles[consumeMember] = keepRegionId;
            keepMembers.add(consumeMember);
        }
        regions.remove(consumeRegionId);

        notifyGenerationListener();
        Utils.maybeWait(this, updateDelay);
    }
}
