package com.alexanderstrada.dun_gen.gen.process;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.tile_map.Direction;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegionColorizer extends BasicGenerator {

    final List<Integer> working = new ArrayList<>();

    public RegionColorizer(Random random) {
        super(random);
    }

    @Override
    public void apply(TileMap tileMap, long updateDelay) {
        super.apply(tileMap, updateDelay);

        for (int i = 0; i < tiles.length; i++) {

            if (tiles[i] == TileMap.FINISHED_TILE) {
                working.clear();

                setMember(i, i, updateDelay);

                while (!working.isEmpty()) {
                    int origin = working.remove(0);

                    for (int neighbor : Utils.getOpenNeighbors(tileMap, origin, Direction.getAll())) {
                        if (tiles[neighbor] == TileMap.FINISHED_TILE) {
                            setMember(neighbor, i, updateDelay);
                        }
                    }
                }
            }
        }
    }

    private void setMember(int newMember, int regionId, long updateDelay) {
        tiles[newMember] = regionId;
        notifyGenerationListener();
        working.add(newMember);
        Utils.maybeWait(this, updateDelay);
    }
}
