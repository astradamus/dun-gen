package com.alexanderstrada.dun_gen.demo;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.DungeonFactory;
import com.alexanderstrada.dun_gen.gen.GenerationListener;
import com.alexanderstrada.dun_gen.tile_map.DefaultTileMap;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

public class GeneratorDemoPanel extends JPanel implements GenerationListener {

    private final Map<Integer, Color> colorCache = new HashMap<>();

    private final long updateDelay;
    private final int squareSize;
    private TileMap tileMap;
    private int displayLayer = 0;

    public GeneratorDemoPanel(long updateDelay,
                              int squareSize,
                              int mapWidth,
                              int mapHeight) {

        this.squareSize = squareSize;
        this.updateDelay = updateDelay;
        setBackground(Color.BLACK);

        tileMap = new DefaultTileMap(mapWidth, mapHeight, 1);

        DungeonFactory.carveBasicDungeon(tileMap, 1, false, false, false, this);
    }

    @Override
    public long getUpdateDelay() {
        return updateDelay;
    }

    @Override
    public void notifyVisualizerMapUpdated() {
        repaint();
    }

    @Override
    public void onGenerationComplete() {
        // Do nothing.
    }

    @Override
    public void notifyVisualizerShowLayer(int layerId) {
        displayLayer = layerId;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.clearRect(0, 0, getWidth(), getHeight());
        if (tileMap != null) {
            int[] colorData = tileMap.getLayer(displayLayer);

            for (int y = 0; y < tileMap.getHeight(); y++) {
                for (int x = 0; x < tileMap.getWidth(); x++) {
                    int value = colorData[Utils.getArrayIndex(x, y, tileMap.getHeight())];
                    int rgb = Math.abs(value);

                    Color color = colorCache.get(rgb);
                    if (color == null) {
                        color = new Color(rgb);
                        colorCache.put(rgb, color);
                    }

                    g.setColor(color);
                    g.fillRect(x* squareSize, y* squareSize, squareSize, squareSize);
                }
            }
        }
    }
}
