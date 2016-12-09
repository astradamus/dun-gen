package com.alexanderstrada.dun_gen.gen.process;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.map.Map;
import com.alexanderstrada.dun_gen.map.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SealSmallestRegions extends BasicGenerator {

    private final java.util.Map<Integer, List<Vector>> regions = new HashMap<>();

    private final int maximumRegions;

    public SealSmallestRegions(Random random, int maximumRegions) {
        super(random);
        this.maximumRegions = maximumRegions;
    }

    @Override
    public void apply(Map map, long updateDelay) {
        super.apply(map, updateDelay);

        regions.clear();
        regions.putAll(map.getRegions());

        List<java.util.Map.Entry<Integer, List<Vector>>> sortedEntries = getSortedEntries();

        int keepCount = Math.min(maximumRegions, sortedEntries.size());

        for (int i = keepCount; i < sortedEntries.size(); i++) {
            java.util.Map.Entry<Integer, List<Vector>> entry = sortedEntries.get(i);
            for (Vector vector : entry.getValue()) {
                tiles[vector.toArrayIndex(height)] = Map.WALL_TILE;
                notifyGenerationListener();
                Utils.maybeWait(this, updateDelay);
            }
            regions.remove(entry.getKey());
        }
    }

    private List<java.util.Map.Entry<Integer, List<Vector>>> getSortedEntries() {
        List<java.util.Map.Entry<Integer, List<Vector>>> sortedEntries = new ArrayList<>();
        List<java.util.Map.Entry<Integer, List<Vector>>> unsortedEntries = new ArrayList<>(regions.entrySet());

        for (java.util.Map.Entry<Integer, List<Vector>> sorting : unsortedEntries) {

            boolean placed = false;
            for (int si = 0; si < sortedEntries.size(); si++) {
                java.util.Map.Entry<Integer, List<Vector>> sorted = sortedEntries.get(si);
                if (sorting.getValue().size() > sorted.getValue().size()) {
                    sortedEntries.add(si, sorting);
                    placed = true;
                    break;
                }
            }
            if (!placed) sortedEntries.add(sorting);
        }
        return sortedEntries;
    }
}
