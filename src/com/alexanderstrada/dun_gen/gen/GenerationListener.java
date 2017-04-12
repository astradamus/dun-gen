package com.alexanderstrada.dun_gen.gen;

public interface GenerationListener {
    void notifyVisualizerShowLayer(int layerId);
    void notifyVisualizerMapUpdated();
    void onGenerationComplete();
    long getUpdateDelay();
}
