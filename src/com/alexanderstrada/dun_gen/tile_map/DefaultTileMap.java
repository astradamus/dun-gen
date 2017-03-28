package com.alexanderstrada.dun_gen.tile_map;

public class DefaultTileMap implements TileMap {

    private final int width;
    private final int height;
    private final int boundary;
    private final int[][] layers;

    public DefaultTileMap(int width, int height, int boundary, int[][] layers) {
        this.width = width;
        this.height = height;
        this.boundary = boundary;
        this.layers = layers;
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
    public int[][] getLayers() {
        return layers;
    }

    @Override
    public int[] getLayer(int layerId) {
        return layers[layerId];
    }

    public static TileMap makeBlank(int width, int height, int boundary) {
        int[][] layers = new int[1][];
        layers[Layer.TILES.id] = new int[width*height];
        return new DefaultTileMap(width, height, boundary, layers);
    }
}
