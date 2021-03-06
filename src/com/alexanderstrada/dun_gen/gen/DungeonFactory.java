package com.alexanderstrada.dun_gen.gen;

import com.alexanderstrada.dun_gen.gen.connect.RandomConnector;
import com.alexanderstrada.dun_gen.gen.connect.RegionConnector;
import com.alexanderstrada.dun_gen.gen.maze.GrowingTreeMaze;
import com.alexanderstrada.dun_gen.gen.process.RegionColorizer;
import com.alexanderstrada.dun_gen.gen.process.SealDeadEnds;
import com.alexanderstrada.dun_gen.gen.process.SealSmallestRegions;
import com.alexanderstrada.dun_gen.gen.room.ProportionalRooms;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.ArrayList;
import java.util.Random;

public class DungeonFactory {

    public static void carveBasicDungeon(TileMap inputMap,
                                         int maxRegionCount,
                                         boolean roomsCanOverlap,
                                         boolean roomsMoreDense,
                                         boolean simplyConnected,
                                         GenerationListener listener) {

        clearMap(inputMap);

        int mapWidth = inputMap.getWidth();
        int mapHeight = inputMap.getHeight();

        Random random = new Random();
        ArrayList<Generator> seq = new ArrayList<>();

        // 1. Generate rooms.
        int maxRoomSize = (int) (Math.sqrt((mapWidth+mapHeight)/2));
        int minRoomSize = 2 + (maxRoomSize / 6);
        double roomDensity = roomsMoreDense ? 0.60 : 0.25;
        ProportionalRooms rooms = new ProportionalRooms(random,
                                                        roomDensity,
                                                        minRoomSize,
                                                        maxRoomSize,
                                                        minRoomSize,
                                                        maxRoomSize,
                                                        roomsCanOverlap);
        seq.add(rooms);

        // 2. Generate hallways.
        int minimumSpacing = maxRoomSize / 2;
        seq.add(new GrowingTreeMaze(random, 0.05, minimumSpacing, 5));

        // 3. Connect hallways/rooms.
        seq.add(new RegionColorizer(random));
        seq.add(new RegionConnector(random));
        if (!simplyConnected) seq.add(new RandomConnector(random, 0.08));

        // 4. Seal dead ends.
        seq.add(new SealDeadEnds(random));

        // 5. Seal isolated regions by size.
        seq.add(new SealSmallestRegions(random, maxRegionCount));

        executeGeneratorSequence(inputMap, seq, listener);
    }

    private static void clearMap(TileMap blankInputMap) {
        int[] tiles = blankInputMap.getLayer(TileMap.Layer.TILES.id);
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = TileMap.TILE_WALL;
        }
    }

    private static void executeGeneratorSequence(final TileMap tileMap,
                                                 final ArrayList<Generator> seq,
                                                 final GenerationListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Generator gen : seq) {
                    gen.setGenerationListener(listener);
                    gen.apply(tileMap);
                    gen.setGenerationListener(null);
                }
                if (listener != null) {
                    listener.onGenerationComplete();
                }
            }
        }).start();
    }
}
