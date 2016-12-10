package com.alexanderstrada.dun_gen.map;

public class DefaultMap implements Map {

    private final int width;
    private final int height;
    private final int boundary;
    private final int[] tiles;

    public DefaultMap(int width, int height, int boundary, int[] tiles) {
        this.width = width;
        this.height = height;
        this.boundary = boundary;
        this.tiles = tiles;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getBoundary() {
        return boundary;
    }

    @Override
    public int[] getTiles() {
        return tiles;
    }
}
