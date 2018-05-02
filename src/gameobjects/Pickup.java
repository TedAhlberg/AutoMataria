package gameobjects;

import gameclient.Game;

import java.util.concurrent.ConcurrentLinkedQueue;

import common.PickupState;

/*
 * @author Dante Håkansson
 */
public abstract class Pickup extends GameObject {
    private static final long serialVersionUID = 1;
    
    protected PickupState state = PickupState.NotTaken;

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

//    public boolean isTaken() {
//        return taken;
//    }
//
//    public boolean isUsed() {
//        return used;
//    }
    public void take(Player player) {
        if(state == state.Taken) {
            return;
        }
        player.setPickUp(this);
        state = state.Taken;
        
    }

    public abstract void use(Player player, ConcurrentLinkedQueue<GameObject> gameObjects);

}
