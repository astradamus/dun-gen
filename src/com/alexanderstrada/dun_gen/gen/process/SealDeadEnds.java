package com.alexanderstrada.dun_gen.gen.process;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;
import com.alexanderstrada.dun_gen.map.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SealDeadEnds extends BasicGenerator {

    private final List<Vector> deadEnds = new ArrayList<>();
    private final List<Vector> deadEndCandidates = new ArrayList<>();
    private final List<Vector> working = new ArrayList<>();

    public SealDeadEnds(Random random) {
        super(random);
    }

    @Override
    public void apply(Map map, long updateDelay) {
        super.apply(map, updateDelay);

        while (true) {
            if (deadEnds.isEmpty()) {
                findDeadEnds();
                if (deadEnds.isEmpty()) {
                    break;
                }
            }

            sealDeadEnds(updateDelay);

            working.addAll(deadEndCandidates);
            deadEndCandidates.clear();

            for (Vector v : working) {
                scanVector(v);
            }

            working.clear();
        }
    }

    private void findDeadEnds() {
        for (int i = 0; i < tiles.length; i++) {
            scanVector(Utils.getVectorFromIndex(i, height));
        }
    }

    private void sealDeadEnds(long updateDelay) {
        for (Vector v : deadEnds) {
            tiles[v.toArrayIndex(height)] = Map.WALL_TILE;
            notifyGenerationListener();
            Utils.maybeWait(this, updateDelay);
        }
        deadEnds.clear();
    }

    private void scanVector(Vector v) {
        if (tiles[v.toArrayIndex(height)] != Map.WALL_TILE) {
            List<Vector> openNeighbors = map.getOpenNeighbors(v, Direction.getCardinals());
            if (openNeighbors.size() <= 1) {
                deadEnds.add(v);
                deadEndCandidates.addAll(openNeighbors);
            }
        }
    }
}
