package com.alexanderstrada.dun_gen.demo;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.GenerationListener;
import com.alexanderstrada.dun_gen.gen.Generator;
import com.alexanderstrada.dun_gen.map.DefaultMap;
import com.alexanderstrada.dun_gen.map.Map;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;

public class GeneratorDemoPanel extends JPanel implements GenerationListener {

    private final java.util.Map<Integer, Color> colorCache = new HashMap<>();

    private int squareSize;
    private Map map;

    public GeneratorDemoPanel(List<Generator> genSequence,
                              int squareSize,
                              int mapWidth,
                              int mapHeight,
                              int updateDelay) {
        this.squareSize = squareSize;
        setBackground(Color.BLACK);

        map = new DefaultMap(mapWidth, mapHeight, 1, new int[mapWidth*mapHeight]);

        new Thread(() -> {

            for (Generator gen : genSequence) {
                gen.setGenerationListener(this);
                gen.apply(map, updateDelay);
                gen.setGenerationListener(null);
            }
        }).start();
    }


    @Override
    public void notifyVisualizerMapUpdated() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.clearRect(0, 0, getWidth(), getHeight());
        if (map != null) {

            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    int value = map.getTiles()[Utils.getArrayIndex(x, y, map.getHeight())];
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
