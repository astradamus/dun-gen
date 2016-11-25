package com.alexanderstrada.dun_gen.gen;

import com.alexanderstrada.dun_gen.map.Map;

public interface Generator {
    void setGenerationListener(com.alexanderstrada.dun_gen.gen.GenerationListener listener);
    void apply(Map map, long updateDelay);
}
