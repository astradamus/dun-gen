package com.alexanderstrada.dun_gen.gen.room;

import com.alexanderstrada.dun_gen.Utils;

public class Room {
    final int x;
    final int y;
    final int width;
    final int height;

    Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    int left() { return x; }
    int right() { return x + width - 1; }
    int top() { return y; }
    int bottom() { return y + height - 1; }

    boolean touches(Room room) {
        boolean intersectsX = right() >= (room.left() - 1) && (room.right() + 1) >= left();
        boolean intersectsY = bottom() >= (room.top() - 1) && (room.bottom() + 1) >= top();
        return intersectsX && intersectsY;
    }

    public int[] getCorners(int mapHeight) {
        int[] corners = new int[4];

        corners[0] = Utils.getArrayIndex(left(), top(), mapHeight);
        corners[1] = Utils.getArrayIndex(right(), top(), mapHeight);
        corners[2] = Utils.getArrayIndex(left(), bottom(), mapHeight);
        corners[3] = Utils.getArrayIndex(right(), bottom(), mapHeight);
        return corners;
    }
}
