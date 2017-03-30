package com.alexanderstrada.dun_gen.gen.connect;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.tile_map.Direction;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RegionConnector extends BasicGenerator {

    private final Map<Integer, List<Integer>> regionsMap = new HashMap<>();

    public RegionConnector(Random random) {
        super(random);
    }

    @Override
    public void apply(TileMap tileMap) {
        super.apply(tileMap);
        notifyGenerationListenerShowLayer(TileMap.Layer.REGIONS.id);
        regionsMap.clear();
        regionsMap.putAll(Utils.getRegions(regions));

        int repeats = 0;
        int lastSize = -1;
        while (repeats < 10) {
            if (regionsMap.size() == lastSize) {
                repeats++;
            }
            else {
                repeats = 0;
                lastSize = regionsMap.size();
            }
            placeConnections(updateDelay);
        }

        notifyGenerationListenerShowLayer(TileMap.Layer.TILES.id);
    }

    private void placeConnections(long updateDelay) {
        for (int i = 0; i < tiles.length; i++) {

            if (tiles[i] == TileMap.TILE_WALL) {

                List<Integer> cardinalNeighbors = Utils.getOpenNeighbors(tileMap, i, Direction.getCardinals());

                if (cardinalNeighbors.size() == 2) {
                    int neighbor1 = cardinalNeighbors.get(0);
                    int n1x = Utils.calcX(neighbor1, height);
                    int n1y = Utils.calcY(neighbor1, height);

                    int neighbor2 = cardinalNeighbors.get(1);
                    int n2x = Utils.calcX(neighbor2, height);
                    int n2y = Utils.calcY(neighbor2, height);

                    // Only make connections at 180 degrees.
                    if (n1x == n2x || n1y == n2y) {

                        int neighbor1Id = regions[neighbor1];
                        int neighbor2Id = regions[neighbor2];

                        boolean neitherIsLink =
                                neighbor1Id != TileMap.TILE_LINK && neighbor2Id != TileMap.TILE_LINK;

                        if (neitherIsLink && neighbor1Id != neighbor2Id && random.nextDouble() < 0.10) {
                            mergeRegions(i, neighbor1Id, neighbor2Id, updateDelay);
                        }
                    }
                }
            }
        }
    }

    private void mergeRegions(int connectionIndex, int keepRegionId, int consumeRegionId, long updateDelay) {

        List<Integer> keepMembers = regionsMap.get(keepRegionId);

        // Add the connection to the kept region.
        tiles[connectionIndex] = TileMap.TILE_LINK;
        regions[connectionIndex] = keepRegionId;
        keepMembers.add(connectionIndex);

        // Add all consumed members to the kept region, update them on the tileMap.
        for (int consumeMember : regionsMap.get(consumeRegionId)) {
            regions[consumeMember] = keepRegionId;
            keepMembers.add(consumeMember);
        }
        regionsMap.remove(consumeRegionId);

        notifyGenerationListener();
        Utils.maybeWait(this, updateDelay);
    }
}
