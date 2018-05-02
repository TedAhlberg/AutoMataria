package gameobjects;

public abstract class InstantPickup extends Pickup {
    private static final long serialVersionUID = 1;

    public InstantPickup() {
        this(0, 0);
    }

    public InstantPickup(int x, int y) {
        super(x, y);
    }

    public void take(Player player) {
        // Players can't take InstantPickups - They should be use() immediately
    }
}
