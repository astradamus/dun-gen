package com.alexanderstrada.dun_gen.gen.process;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SealDeadEnds extends BasicGenerator {

    private final List<Integer> deadEnds = new ArrayList<>();
    private final List<Integer> deadEndCandidates = new ArrayList<>();
    private final List<Integer> working = new ArrayList<>();

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

            for (int i : working) {
                scanIndex(i);
            }

            working.clear();
        }
    }

    private void findDeadEnds() {
        for (int i = 0; i < tiles.length; i++) {
            scanIndex(i);
        }
    }

    private void sealDeadEnds(long updateDelay) {
        for (int i : deadEnds) {
            tiles[i] = Map.WALL_TILE;
            notifyGenerationListener();
            Utils.maybeWait(this, updateDelay);
        }
        deadEnds.clear();
    }

    private void scanIndex(int i) {
        if (tiles[i] != Map.WALL_TILE) {
            List<Integer> openNeighbors = Utils.getOpenNeighbors(map, i, Direction.getCardinals());
            if (openNeighbors.size() <= 1) {
                deadEnds.add(i);
                deadEndCandidates.addAll(openNeighbors);
            }
        }
    }
}
