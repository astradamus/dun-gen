package com.alexanderstrada.dun_gen.gen.room;

import com.alexanderstrada.dun_gen.map.Vector;

public class Room {
    final Vector origin;
    final int width;
    final int height;

    Room(Vector origin, int width, int height) {
        this.origin = origin;
        this.width = width;
        this.height = height;
    }

    int left() { return origin.getX(); }
    int right() { return origin.getX() + width; }
    int top() { return origin.getY(); }
    int bottom() { return origin.getY() + height; }

    boolean intersects(Room room) {
        boolean intersectsX = right() >= room.left() && room.right() >= left();
        boolean intersectsY = bottom() >= room.top() && room.bottom() >= top();
        return intersectsX && intersectsY;
    }
}
