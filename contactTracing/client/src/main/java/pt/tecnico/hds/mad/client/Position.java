package pt.tecnico.hds.mad.client;

import java.util.Objects;

public class Position {
    private int MAX_DIST = 10; // for now, check back later
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isNear(Position otherPos) {
        return Math.abs(otherPos.x - this.x) + Math.abs(otherPos.y - this.y) <= MAX_DIST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return getX() == position.getX() && getY() == position.getY();
    }
}
