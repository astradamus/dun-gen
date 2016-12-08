package com.alexanderstrada.dun_gen.gen;

import com.alexanderstrada.dun_gen.map.Map;

import java.util.Random;

public abstract class BasicGenerator implements Generator {

    final Random random;

    private GenerationListener listener;
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
    public void apply(Map map, long updateDelay) {
        width = map.getWidth();
        height = map.getHeight();
        boundary = map.getBoundary();
        tiles = map.getTiles();
    }
}
