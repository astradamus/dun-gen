package com.alexanderstrada.dun_gen.demo;

import com.alexanderstrada.dun_gen.gen.Generator;
import com.alexanderstrada.dun_gen.gen.connect.RegionConnector;
import com.alexanderstrada.dun_gen.gen.maze.GrowingTreeMaze;
import com.alexanderstrada.dun_gen.gen.process.SealSmallestRegions;
import com.alexanderstrada.dun_gen.gen.process.SealDeadEnds;
import com.alexanderstrada.dun_gen.gen.room.ProportionalRooms;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    static int demoNumber = 0;

    public static void main(String[] args) {

        int squareSize;
        int mapWidth;
        int mapHeight;
        int updateDelay;
        int minRoomSize;
        int maxRoomSize;

        switch (demoNumber) {
            case 0:
                squareSize = 10;
                mapWidth = 75;
                mapHeight = 75;
                updateDelay = 8;
                minRoomSize = 3;
                maxRoomSize = 9;
                break;
            case 1:
                squareSize = 2;
                mapWidth = 400;
                mapHeight = 400;
                updateDelay = 1;
                minRoomSize = 5;
                maxRoomSize = 15;
                break;
            case 2:
                squareSize = 1;
                mapWidth = 1800;
                mapHeight = 900;
                updateDelay = 0;
                minRoomSize = 9;
                maxRoomSize = 21;
                break;
            default: throw new IllegalArgumentException();
        }

        Random random = new Random();
        ArrayList<Generator> genSequence = new ArrayList<>();
        genSequence.add(new ProportionalRooms(random, 0.25, minRoomSize, maxRoomSize, minRoomSize, maxRoomSize, false));
        genSequence.add(new GrowingTreeMaze(random, 0.05, 8, 5));
        genSequence.add(new RegionConnector(random));
        genSequence.add(new SealDeadEnds(random));
        genSequence.add(new SealSmallestRegions(random, 1));

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
