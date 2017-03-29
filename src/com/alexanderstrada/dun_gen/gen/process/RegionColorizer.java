package com.alexanderstrada.dun_gen.gen.process;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.tile_map.Direction;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegionColorizer extends BasicGenerator {

    private final List<Integer> working = new ArrayList<>();

    public RegionColorizer(Random random) {
        super(random);
    }

    @Override
    public void apply(TileMap tileMap) {
        super.apply(tileMap);
        notifyGenerationListenerShowLayer(TileMap.Layer.REGIONS.id);
        clearRegionsLayer();

        for (int i = 0; i < tiles.length; i++) {

            if (isOpenAndUnassigned(i)) {
                working.clear();

                setMember(i, i, updateDelay);

                while (!working.isEmpty()) {
                    int origin = working.remove(0);

                    for (int neighbor : Utils.getOpenNeighbors(tileMap, origin, Direction.getAll())) {
                        if (isOpenAndUnassigned(neighbor)) {
                            setMember(neighbor, i, updateDelay);
                        }
                    }
                }
            }
        }

        notifyGenerationListenerShowLayer(TileMap.Layer.TILES.id);
    }

    private void clearRegionsLayer() {
        for (int i = 0; i < regions.length; i++) {
            regions[i] = -1;
        }
    }

    private boolean isOpenAndUnassigned(int i) {
        return regions[i] == -1 && tiles[i] == TileMap.TILE_FINISHED;
    }

    private void setMember(int newMember, int regionId, long updateDelay) {
        regions[newMember] = regionId;
        notifyGenerationListener();
        working.add(newMember);
        Utils.maybeWait(this, updateDelay);
    }
}
