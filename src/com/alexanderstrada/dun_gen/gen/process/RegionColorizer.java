package com.alexanderstrada.dun_gen.gen.process;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;
import com.alexanderstrada.dun_gen.map.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegionColorizer extends BasicGenerator {

    final List<Vector> working = new ArrayList<>();

    public RegionColorizer(Random random) {
        super(random);
    }

    @Override
    public void apply(Map map, long updateDelay) {
        super.apply(map, updateDelay);

        for (int i = 0; i < tiles.length; i++) {
            Vector v = Utils.getVectorFromIndex(i, height);

            if (tiles[i] == Map.FINISHED_TILE) {
                working.clear();

                setMember(v, i, updateDelay);

                while (!working.isEmpty()) {
                    Vector origin = working.remove(0);

                    for (Vector neighbor : map.getOpenNeighbors(origin, Direction.getAll())) {
                        if (tiles[neighbor.toArrayIndex(height)] == Map.FINISHED_TILE) {
                            setMember(neighbor, i, updateDelay);
                        }
                    }
                }
            }
        }
    }

    private void setMember(Vector newMember, int regionId, long updateDelay) {
        tiles[newMember.toArrayIndex(height)] = regionId;
        notifyGenerationListener();
        working.add(newMember);
        Utils.maybeWait(this, updateDelay);
    }
}
