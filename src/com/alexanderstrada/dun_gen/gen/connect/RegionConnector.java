package com.alexanderstrada.dun_gen.gen.connect;

import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.Random;

public class RegionConnector extends Connector {

    public RegionConnector(Random random) {
        super(random);
    }

    @Override
    public void apply(TileMap tileMap) {
        super.apply(tileMap);
        notifyGenerationListenerShowLayer(TileMap.Layer.REGIONS.id);

        int repeats = 0;
        int lastSize = -1;
        while (repeats < 10) {
            if (getRegionsCount() == lastSize) {
                repeats++;
            }
            else {
                repeats = 0;
                lastSize = getRegionsCount();
            }
            placeConnections();
        }

        notifyGenerationListenerShowLayer(TileMap.Layer.TILES.id);
    }

    @Override
    protected boolean isLinkValid(int neighbor1Id, int neighbor2Id) {
        boolean superIsValid = super.isLinkValid(neighbor1Id, neighbor2Id);
        boolean notSameRegion = neighbor1Id != neighbor2Id;
        return superIsValid && notSameRegion && random.nextDouble() < 0.10;
    }
}
