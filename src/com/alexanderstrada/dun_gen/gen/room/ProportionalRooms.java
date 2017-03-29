package com.alexanderstrada.dun_gen.gen.room;

import com.alexanderstrada.dun_gen.Utils;
import com.alexanderstrada.dun_gen.gen.BasicGenerator;
import com.alexanderstrada.dun_gen.tile_map.Direction;
import com.alexanderstrada.dun_gen.tile_map.TileMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProportionalRooms extends BasicGenerator {

    private final List<Room> rooms = new ArrayList<>();

    private double roomDensity;
    private int minRoomWidth;
    private int maxRoomWidth;
    private int minRoomHeight;
    private int maxRoomHeight;
    private boolean allowCompoundRooms;

    public ProportionalRooms(Random random) {
        super(random);
    }

    public ProportionalRooms(Random random,
                             double roomDensity,
                             int minRoomWidth,
                             int maxRoomWidth,
                             int minRoomHeight,
                             int maxRoomHeight,
                             boolean allowCompoundRooms) {
        super(random);
        this.roomDensity = roomDensity;
        this.minRoomWidth = minRoomWidth;
        this.minRoomHeight = minRoomHeight;
        this.maxRoomWidth = maxRoomWidth;
        this.maxRoomHeight = maxRoomHeight;
        this.allowCompoundRooms = allowCompoundRooms;
    }

    public double getRoomDensity() {
        return roomDensity;
    }

    public void setRoomDensity(double roomDensity) {
        this.roomDensity = roomDensity;
    }

    public int getMinRoomWidth() {
        return minRoomWidth;
    }

    public void setMinRoomWidth(int minRoomWidth) {
        this.minRoomWidth = minRoomWidth;
    }

    public int getMaxRoomWidth() {
        return maxRoomWidth;
    }

    public void setMaxRoomWidth(int maxRoomWidth) {
        this.maxRoomWidth = maxRoomWidth;
    }

    public int getMinRoomHeight() {
        return minRoomHeight;
    }

    public void setMinRoomHeight(int minRoomHeight) {
        this.minRoomHeight = minRoomHeight;
    }

    public int getMaxRoomHeight() {
        return maxRoomHeight;
    }

    public void setMaxRoomHeight(int maxRoomHeight) {
        this.maxRoomHeight = maxRoomHeight;
    }

    public boolean getAllowCompoundRooms() {
        return allowCompoundRooms;
    }

    public void setAllowCompoundRooms(boolean allowCompoundRooms) {
        this.allowCompoundRooms = allowCompoundRooms;
    }

    @Override
    public void apply(TileMap tileMap) {
        super.apply(tileMap);
        rooms.clear();

        int open = (int) (tiles.length * roomDensity);

        while (open > 0) {
            Utils.maybeWait(this, updateDelay);

            int roomWidth = Utils.randomIntInRange(random, minRoomWidth, maxRoomWidth);
            int roomHeight = Utils.randomIntInRange(random, minRoomHeight, maxRoomHeight);

            int originX = Utils.randomIntInRange(random, boundary, width - roomWidth - boundary);
            int originY = Utils.randomIntInRange(random, boundary, height - roomHeight - boundary);

            int originI = Utils.getArrayIndex(originX, originY, height);

            Room room = new Room(originX, originY, roomWidth, roomHeight);

            if (!validateRoom(room)) {
                open--; // Ensure we do not loop forever.
                continue;
            }

            // Carve the room.
            for (int y = 0; y < roomHeight; y++) {
                for (int x = 0; x < roomWidth; x++) {
                    int i = originI + Utils.getArrayIndex(x, y, height);
                    if (tiles[i] == TileMap.TILE_WALL) {
                        tiles[i] = TileMap.TILE_ROOM;
                        open--;
                    }
                }
            }

            rooms.add(room);
            notifyGenerationListener();
        }
    }

    private boolean validateRoom(Room room) {

        // If compound rooms are not allowed, reject this room if it creates one.
        if (!allowCompoundRooms) {
            for (Room compare : rooms) {
                if (compare.touches(room)) {
                    return false;
                }
            }
        }

        // If compound rooms are allowed, reject diagonal connections.
        else {
            int[] corners = room.getCorners(height);
            List<Direction> diagonals = Direction.getDiagonals();

            for (int cI = 0; cI < corners.length; cI++) {
                int corner = corners[cI];
                Direction out = diagonals.get(cI);

                int potentialDiagonalConnection = corner + out.get2dIndexOffset(height);

                if (tiles[potentialDiagonalConnection] != TileMap.TILE_WALL) {

                    int leftI = corner + out.left().get2dIndexOffset(height);
                    int rightI = corner + out.right().get2dIndexOffset(height);

                    if (tiles[leftI] == TileMap.TILE_WALL && tiles[rightI] == TileMap.TILE_WALL) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
