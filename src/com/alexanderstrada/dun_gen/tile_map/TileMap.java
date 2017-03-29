package com.alexanderstrada.dun_gen.tile_map;

public interface TileMap {
    int TILE_HIGHLIGHT = -0xFF0000;
    int TILE_WORKING = -0xFFFFFF;
    int TILE_WORKED = -0x666666;
    int TILE_HALL = -0x004444;
    int TILE_ROOM = -0x444466;
    int TILE_LINK = -0x226622;
    int TILE_WALL = 0x000000;

    enum Layer {
        TILES(0),
        REGIONS(1);

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
