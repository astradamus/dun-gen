package com.alexanderstrada.dun_gen.map;

public interface Map {
    int HIGHLIGHT_TILE = -0xFF0000;
    int WORKING_TILE = -0xFFFFFF;
    int WORKED_TILE = -0x666666;
    int FINISHED_TILE = -0x333333;
    int WALL_TILE = 0x000000;

    int getWidth();
    int getHeight();
    int getBoundary();
    int[] getTiles();
}
