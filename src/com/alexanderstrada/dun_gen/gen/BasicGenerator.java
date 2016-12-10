package com.alexanderstrada.dun_gen.gen;

import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.Random;

public abstract class BasicGenerator implements Generator {

    protected final Random random;

    private GenerationListener listener;
    protected TileMap tileMap;
    protected int width;
    protected int height;
    protected int boundary;
    protected int[] tiles;

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

    @Override
    public void apply(TileMap tileMap, long updateDelay) {
        this.tileMap = tileMap;
        width = tileMap.getWidth();
        height = tileMap.getHeight();
        boundary = tileMap.getBoundary();
        tiles = tileMap.getTiles();
    }
}
