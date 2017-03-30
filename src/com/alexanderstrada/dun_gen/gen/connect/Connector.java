package com.alexanderstrada.dun_gen.gen.connect;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.tile_map.Direction;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Connector extends BasicGenerator {

    private final Map<Integer, List<Integer>> regionsMap = new HashMap<>();

    public Connector(Random random) {
        super(random);
    }

    protected int getRegionsCount() {
        return regionsMap.size();
    }

    @Override
    public void apply(TileMap tileMap) {
        super.apply(tileMap);
        regionsMap.clear();
        regionsMap.putAll(Utils.getRegions(regions));
    }

    protected void placeConnections() {
        for (int i = 0; i < tiles.length; i++) {
            placeConnection(i);
        }
    }

    protected void placeConnection(int i) {

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

                    if (isLinkValid(neighbor1Id, neighbor2Id)) {
                        connect(i, neighbor1Id, neighbor2Id);
                    }
                }
            }
        }
    }

    protected boolean isLinkValid(int neighbor1Id, int neighbor2Id) {
        return neighbor1Id != TileMap.TILE_LINK && neighbor2Id != TileMap.TILE_LINK;
    }

    private void connect(int connectionIndex, int neighbor1Id, int neighbor2Id) {

        List<Integer> keepMembers = regionsMap.get(neighbor1Id);

        // Add the connection to the kept region.
        tiles[connectionIndex] = TileMap.TILE_LINK;
        regions[connectionIndex] = neighbor1Id;
        keepMembers.add(connectionIndex);

        // If members represent different regions, add all consumed members to the kept region.
        if (neighbor1Id != neighbor2Id) {
            for (int consumeMember : regionsMap.get(neighbor2Id)) {
                regions[consumeMember] = neighbor1Id;
                keepMembers.add(consumeMember);
            }
            regionsMap.remove(neighbor2Id);
        }

        notifyGenerationListener();
        Utils.maybeWait(this, updateDelay);
    }
}
