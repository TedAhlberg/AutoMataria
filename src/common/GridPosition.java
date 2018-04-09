package common;

import java.io.Serializable;

/**
 * @author Johannes Blüml
 */
public class GridPosition implements Serializable {
    private int x, y, id;

    public GridPosition(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(Object o) {
        return o instanceof GridPosition && x == ((GridPosition) o).getX() && y == ((GridPosition) o).getY();
    }
}
