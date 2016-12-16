package com.alexanderstrada.dun_gen.gen;

import com.alexanderstrada.dun_gen.tile_map.TileMap;

public interface Generator {
    void setGenerationListener(com.alexanderstrada.dun_gen.gen.GenerationListener listener);
    void apply(TileMap tileMap);
}
