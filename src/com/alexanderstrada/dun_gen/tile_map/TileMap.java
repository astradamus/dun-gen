package com.alexanderstrada.dun_gen.tile_map;

public interface TileMap {
    int TILE_HIGHLIGHT = -0xFF0000;
    int TILE_WORKING = -0xFFFFFF;
    int TILE_WORKED = -0x666666;
    int TILE_FINISHED = -0x333333;
    int TILE_WALL = 0x000000;

    enum Layer {

        TILES(0);

        public final int id;

        Layer(int id) {
            this.id = id;
        }
    }

    int getWidth();
    int getHeight();
    int getBoundary();
    int[][] getLayers();
    int[] getLayer(int layerId);
}
