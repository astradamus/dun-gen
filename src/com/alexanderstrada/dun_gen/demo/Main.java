package com.alexanderstrada.dun_gen.demo;

import com.alexanderstrada.dun_gen.gen.Generator;
import com.alexanderstrada.dun_gen.gen.GrowingTreeMazeGen;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    static int demoNumber = 4;

    public static void main(String[] args) {

        int squareSize;
        int mapWidth;
        int mapHeight;
        int updateDelay;

        switch (demoNumber) {
            case 0:
                squareSize = 64;
                mapWidth = 13;
                mapHeight = 13;
                updateDelay = 500;
                break;
            case 1:
                squareSize = 32;
                mapWidth = 25;
                mapHeight = 25;
                updateDelay = 75;
                break;
            case 2:
                squareSize = 10;
                mapWidth = 75;
                mapHeight = 75;
                updateDelay = 8;
                break;
            case 3:
                squareSize = 2;
                mapWidth = 400;
                mapHeight = 400;
                updateDelay = 1;
                break;
            case 4:
                squareSize = 1;
                mapWidth = 1800;
                mapHeight = 900;
                updateDelay = 0;
                break;
            default: throw new IllegalArgumentException();
        }

        Random random = new Random();
        ArrayList<Generator> genSequence = new ArrayList<>();
        genSequence.add(new GrowingTreeMazeGen(random, 1, 0.75));

        JPanel panel = new GeneratorDemoPanel(genSequence, squareSize, mapWidth, mapHeight, updateDelay);
        runDemo(squareSize, mapWidth, mapHeight, panel);
    }

    private static void runDemo(int squareSize, int mapWidth, int mapHeight, JPanel panel) {
        final JFrame window = new JFrame("Dungeon Generation");
        window.setResizable(false);
        panel.setPreferredSize(new Dimension(squareSize*mapWidth, squareSize*mapHeight));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setContentPane(panel);
        window.pack();
        window.setVisible(true);
    }
}
