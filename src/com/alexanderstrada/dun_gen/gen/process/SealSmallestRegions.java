package com.alexanderstrada.dun_gen.gen.process;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SealSmallestRegions extends BasicGenerator {

    private final Map<Integer, List<Integer>> regionsMap = new HashMap<>();

    private final int maximumRegions;

    public SealSmallestRegions(Random random, int maximumRegions) {
        super(random);
        this.maximumRegions = maximumRegions;
    }

    @Override
    public void apply(TileMap tileMap) {
        super.apply(tileMap);
        notifyGenerationListenerShowLayer(TileMap.Layer.REGIONS.id);
        regionsMap.clear();
        regionsMap.putAll(Utils.getRegions(regions));

        List<java.util.Map.Entry<Integer, List<Integer>>> sortedEntries = getSortedEntries();

        int keepCount = Math.min(maximumRegions, sortedEntries.size());

        for (int i = keepCount; i < sortedEntries.size(); i++) {
            java.util.Map.Entry<Integer, List<Integer>> entry = sortedEntries.get(i);
            for (int index : entry.getValue()) {
                seal(index);
            }
            regionsMap.remove(entry.getKey());
        }

        notifyGenerationListenerShowLayer(TileMap.Layer.TILES.id);
    }

    private List<java.util.Map.Entry<Integer, List<Integer>>> getSortedEntries() {
        List<java.util.Map.Entry<Integer, List<Integer>>> sortedEntries = new ArrayList<>();
        List<java.util.Map.Entry<Integer, List<Integer>>> unsortedEntries = new ArrayList<>(regionsMap.entrySet());

        for (java.util.Map.Entry<Integer, List<Integer>> sorting : unsortedEntries) {

            boolean placed = false;
            for (int si = 0; si < sortedEntries.size(); si++) {
                java.util.Map.Entry<Integer, List<Integer>> sorted = sortedEntries.get(si);
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

    private void seal(int index) {
        tiles[index] = TileMap.TILE_WALL;
        regions[index] = -1;
        notifyGenerationListener();
        Utils.maybeWait(this, updateDelay);
    }
}
