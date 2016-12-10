package com.alexanderstrada.dun_gen.gen.room;

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
    int right() { return x + width; }
    int top() { return y; }
    int bottom() { return y + height; }

    boolean intersects(Room room) {
        boolean intersectsX = right() >= room.left() && room.right() >= left();
        boolean intersectsY = bottom() >= room.top() && room.bottom() >= top();
        return intersectsX && intersectsY;
    }
}
