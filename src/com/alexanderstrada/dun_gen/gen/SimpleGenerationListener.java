package com.alexanderstrada.dun_gen.gen;

public class SimpleGenerationListener implements GenerationListener {
    @Override
    public void notifyVisualizerShowLayer(int layerId) { }

    @Override
    public void notifyVisualizerMapUpdated() { }

    @Override
    public void onGenerationComplete() { }

    @Override
    public long getUpdateDelay() { return 0; }
}
