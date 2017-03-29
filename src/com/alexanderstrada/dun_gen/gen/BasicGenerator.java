package com.alexanderstrada.dun_gen.gen;

import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.Random;

public abstract class BasicGenerator implements Generator {

    protected final Random random;

    private GenerationListener listener;
    protected long updateDelay;

    protected TileMap tileMap;
    protected int width;
    protected int height;
    protected int boundary;
    protected int[] tiles;
    protected int[] regions;
    protected int[][] layers;

    public BasicGenerator(Random random) {
        this.random = random;
    }

    @Override
    public void setGenerationListener(GenerationListener listener) {
        this.listener = listener;
    }

    protected void notifyGenerationListener() {
        if (listener != null) listener.notifyVisualizerMapUpdated();
    }

    protected void notifyGenerationListenerShowLayer(int layerId) {
        if (listener != null) listener.notifyVisualizerShowLayer(layerId);
    }

    @Override
    public void apply(TileMap tileMap) {
        updateDelay = listener == null ? 0 : listener.getUpdateDelay();
        this.tileMap = tileMap;
        width = tileMap.getWidth();
        height = tileMap.getHeight();
        boundary = tileMap.getBoundary();
        tiles = tileMap.getLayer(TileMap.Layer.TILES.id);
        regions = tileMap.getLayer(TileMap.Layer.REGIONS.id);
        layers = tileMap.getLayers();
    }
}
