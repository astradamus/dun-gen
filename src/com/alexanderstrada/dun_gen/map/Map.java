package com.alexanderstrada.dun_gen.map;

public class Map {

    public static final int HIGHLIGHT_TILE = 0xFF0000;
    public static final int WORKING_TILE = 0xFFFFFF;
    public static final int FINISHED_TILE = 0x333333;
    public static final int WALL_TILE = 0x000000;

    private final int width;
    private final int height;
    private final int[] tiles;

    public Map(int width, int height, int[] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getTiles() {
        return tiles;
    }
}
