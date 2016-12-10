package com.alexanderstrada.dun_gen.gen.process;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegionColorizer extends BasicGenerator {

    final List<Integer> working = new ArrayList<>();

    public RegionColorizer(Random random) {
        super(random);
    }

    @Override
    public void apply(Map map, long updateDelay) {
        super.apply(map, updateDelay);

        for (int i = 0; i < tiles.length; i++) {

            if (tiles[i] == Map.FINISHED_TILE) {
                working.clear();

                setMember(i, i, updateDelay);

                while (!working.isEmpty()) {
                    int origin = working.remove(0);

                    for (int neighbor : Utils.getOpenNeighbors(map, origin, Direction.getAll())) {
                        if (tiles[neighbor] == Map.FINISHED_TILE) {
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
