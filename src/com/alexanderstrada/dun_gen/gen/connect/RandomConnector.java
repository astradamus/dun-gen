package com.alexanderstrada.dun_gen.gen.connect;

import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.Random;

public class RandomConnector extends Connector {

    private double connectionChance;

    public RandomConnector(Random random, double connectionChance) {
        super(random);
        this.connectionChance = connectionChance;
    }

    public double getConnectionChance() {
        return connectionChance;
    }

    public void setConnectionChance(double connectionChance) {
        this.connectionChance = connectionChance;
    }

    @Override
    public void apply(TileMap tileMap) {
        super.apply(tileMap);
        placeConnections();
    }

    @Override
    protected boolean isLinkValid(int neighbor1Id, int neighbor2Id) {
        return super.isLinkValid(neighbor1Id, neighbor2Id) && random.nextDouble() < connectionChance;
    }
}
