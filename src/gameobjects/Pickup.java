package gameobjects;

import common.PickupState;
import gameclient.Game;

import java.util.Collection;

/*
 * @author Dante Håkansson
 */
public abstract class Pickup extends GameObject {
    private static final long serialVersionUID = 1;

    protected PickupState state = PickupState.NotTaken;
    protected Player player;

    public Pickup() {
        this(0, 0);
    }

    public Pickup(int x, int y) {
        super(x, y);
        width = (int) (Game.GRID_PIXEL_SIZE * 1.5);
        height = width;
    }

    public void tick() {
    }

    public PickupState getState() {
        return state;
    }

    public void setState(PickupState state) {
        this.state = state;
    }

    public void take(Player player) {
        if (state == state.Taken) {
            return;
        }
        player.setPickUp(this);
        this.player = player;
        state = state.Taken;
    }

    public abstract void use(Player player, Collection<GameObject> gameObjects);
}
