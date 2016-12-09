package com.alexanderstrada.dun_gen.gen.connect;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.process.RegionColorizer;
import com.alexanderstrada.dun_gen.map.Direction;
import com.alexanderstrada.dun_gen.map.Map;
import com.alexanderstrada.dun_gen.map.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RegionConnector extends RegionColorizer {

    private final java.util.Map<Integer, List<Vector>> regions = new HashMap<>();

    public RegionConnector(Random random) {
        super(random);
    }

    @Override
    public void apply(Map map, long updateDelay) {
        super.apply(map, updateDelay);

        regions.clear();
        collectRegions();

        int repeats = 0;
        int lastSize = -1;
        while (repeats < 10) {
            if (regions.size() == lastSize) {
                repeats++;
            }
            else {
                repeats = 0;
                lastSize = regions.size();
            }
            placeConnections(updateDelay);
        }
    }

    private void collectRegions() {

        for (int i = 0; i < tiles.length; i++) {
            int identity = tiles[i];

            if (!regions.containsKey(identity)) {
                regions.put(identity, new ArrayList<>());
            }

            regions.get(identity).add(Utils.getVectorFromIndex(i, height));
        }
    }

    private void placeConnections(long updateDelay) {
        for (int i = 0; i < tiles.length; i++) {
            Vector v = Utils.getVectorFromIndex(i, height);

            if (tiles[i] == Map.WALL_TILE) {

                List<Vector> openCards = map.getOpenNeighbors(v, Direction.getCardinals());

                if (openCards.size() == 2) {

                    Vector c1 = openCards.get(0);
                    Vector c2 = openCards.get(1);

                    // Only make connections at 180 degrees.
                    if (c1.getX() == c2.getX() || c1.getY() == c2.getY()) {

                        int c1i = tiles[c1.toArrayIndex(height)];
                        int c2i = tiles[c2.toArrayIndex(height)];

                        if (c1i != c2i && random.nextDouble() < 0.10) {
                            mergeRegions(v, c1i, c2i, updateDelay);
                        }
                    }
                }
            }
        }
    }

    private void mergeRegions(Vector connection, int regionToKeep, int regionToConsume, long updateDelay) {
        List<Vector> keepMembers = regions.get(regionToKeep);
        for (Vector member : regions.get(regionToConsume)) {
            tiles[member.toArrayIndex(height)] = regionToKeep;
            keepMembers.add(member);
        }
        regions.remove(regionToConsume);

        tiles[connection.toArrayIndex(height)] = regionToKeep;
        keepMembers.add(connection);
        notifyGenerationListener();
        Utils.maybeWait(this, updateDelay);
    }
}
