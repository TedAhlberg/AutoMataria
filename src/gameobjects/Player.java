package gameobjects;

import common.*;
import common.messages.*;
import gameclient.Game;
import gameclient.Resources;

import java.awt.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Johannes Blüml
 * @author Dante Håkansson
 */
public class Player extends GameObject {
    private static final long serialVersionUID = 4;

    transient private final Collection<GameObject> gameObjects;
    transient private final ConcurrentLinkedQueue<Direction> inputQueue = new ConcurrentLinkedQueue<>();
    transient private final Trail trail;
    private final String name;
    transient private GameMap currentMap;
    transient private Direction previousDirection;
    transient private MessageListener listener;
    private Pickup pickupSlot;
    private String image;
    private Color color;
    private boolean dead, ready, invincible, reversed;

    public Player(String name, Collection<GameObject> gameObjects, GameMap currentMap) {
        this.name = name;
        this.gameObjects = gameObjects;
        this.currentMap = currentMap;
        color = Color.LIGHT_GRAY;
        width = Game.GRID_PIXEL_SIZE;
        height = Game.GRID_PIXEL_SIZE;
        previousDirection = direction;
        trail = new Trail(this);
        trail.setId(ID.getNext());
    }

    /**
     * Temporary method for debugging purposes
     *
     * @TODO: REMOVE THIS METHOD
     */
    public void setSpeed(int speed) {
        super.setSpeed(speed);
        listener.newMessage(new ChatMessage("My speed is now " + speed + "ppt.", this));
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public void render(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x - 10, y - 10, width + 20, height + 20);
        g.setColor(color.brighter());
        g.drawRect(x - 10, y - 10, width + 20, height + 20);

        if (dead) {
            image = "DeadSkull.png";
        } else {
            if (invincible) {
                image = "TransparentInvinciblePickup.png";
                g.drawRect(x - 40, y - 40, width + 80, height + 80);
            }
            if (reversed) image = "TransparentReversePickup.png";
        }

        if (image != null) g.drawImage(Resources.getImage(image), x, y, width, height, null);

        // Display Player name
        g.setColor(color.darker());
        g.setFont(Resources.defaultFont.deriveFont(80f));
        FontMetrics fontMetrics = g.getFontMetrics(g.getFont());
        int stringWidth = fontMetrics.stringWidth(name);
        int xName = x + (Game.GRID_PIXEL_SIZE / 2) - (stringWidth / 2);
        int yName = y - (height / 2);
        g.drawString(name.toUpperCase(), xName, yName);

        // Draw players pickup
        if (pickupSlot != null && pickupSlot.getState() == PickupState.Taken) {
            int xPickup = xName - (width + width / 4);
            int yPickup = (Game.GRID_PIXEL_SIZE / 5) + yName - height;
            g.drawImage(Resources.getImage(pickupSlot.getClass().getSimpleName() + ".png"), xPickup, yPickup, width, height, null);
        }
    }

    public void tick() {
        if (dead) return;

        updateDirection();
        checkCollisions();
    }

    private boolean teleportIfOutsideMap() {
        Dimension map = Utility.convertFromGrid(currentMap.getGrid());
        if (x <= -width) {
            x += map.width;
            return true;
        } else if (x >= map.width) {
            x -= map.width + width;
            return true;
        } else if (y <= -height) {
            y += map.height;
            return true;
        } else if (y >= map.height) {
            y -= map.height + height;
            return true;
        }
        return false;
    }

    private void updateDirection() {
        previousDirection = direction;

        if (inputQueue.isEmpty()) {
            move(speed);
            return;
        }

        while (inputQueue.peek() == direction || inputQueue.size() > 2) {
            // Remove unnessesary multiple keypress in same direction
            // Remove all keypress but the last 2 to avoid excessive input delay
            inputQueue.remove();
        }

        while (!invincible && inputQueue.peek() == Utility.getOppositeDirection(direction)) {
            // Remove keypress in the opposite direction (you would die instantly if you are not invincible)
            inputQueue.remove();
        }

        if (inputQueue.isEmpty()) {
            move(speed);
            return;
        }

        int canMoveIn = Utility.canChangeDirection(direction, getPoint(), speed);
        if (canMoveIn > 0) {
            // First moves player to closest grid bounary
            move(canMoveIn);
            previousDirection = direction;
            direction = inputQueue.remove();
            // Then moves in the new direction
            move(speed - canMoveIn);

        } else if (canMoveIn == 0 || direction == Direction.Static) {
            // Moves player forward in the new direction
            previousDirection = direction;
            direction = inputQueue.remove();
            move(speed);
            listener.newMessage(new PlayerMessage(PlayerMessage.Event.Moved, this));
        } else {
            // No direction change so just continue moving
            move(speed);
        }
    }

    public void move(int amount) {
        Point previousPosition = new Point(x, y);
        switch (direction) {
            case Up:
                y -= amount;
                break;
            case Down:
                y += amount;
                break;
            case Left:
                x -= amount;
                break;
            case Right:
                x += amount;
                break;
        }
        boolean hasTeleported = teleportIfOutsideMap();
        if (!hasTeleported && !invincible) {
            Point newPosition = new Point(x, y);
            trail.grow(previousPosition, newPosition);
        }
    }

    private void checkCollisions() {
        Rectangle playerRectangle = getBounds();
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Wall) {

                if (((Wall) gameObject).intersects(playerRectangle)) {
                    setDead(true);
                }

            } else if (playerRectangle.intersects(gameObject.getBounds())) {

                if (gameObject instanceof Player) {
                    if (gameObject.equals(this)) continue;

                    setDead(true);
                    ((Player) gameObject).setDead(true);

                } else if (gameObject instanceof Pickup) {

                    Pickup pickup = (Pickup) gameObject;
                    if (pickup.getState() != PickupState.NotTaken) continue;

                    if (pickup instanceof InstantPickup) {
                        pickup.use(this, gameObjects);
                        listener.newMessage(new PlayerPickupMessage(PlayerPickupMessage.Event.PickupUsed, this, pickup));
                    } else {
                        if (pickupSlot != null) {
                            pickupSlot.done();
                        }
                        pickup.take(this, gameObjects);
                        listener.newMessage(new PlayerPickupMessage(PlayerPickupMessage.Event.PickupTaken, this, pickup));
                    }
                }
            }
        }
    }

    /**
     * Resets player to a good state
     * used when new games or warmups start
     * so nothing strange remains when the player spawns
     */
    public void reset() {
        if (pickupSlot != null) pickupSlot.done();

        inputQueue.clear();
        direction = previousDirection = Direction.Static;
        dead = invincible = reversed = false;

        trail.clear();
    }

    public void setPickUp(Pickup pickup) {
        this.pickupSlot = pickup;
    }

    public void usePickup() {
        if (pickupSlot != null && pickupSlot.getState() == PickupState.Taken) {
            listener.newMessage(new PlayerPickupMessage(PlayerPickupMessage.Event.PickupUsed, this, pickupSlot));
            pickupSlot.use(this, gameObjects);
        }
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public void setNextDirection(Direction direction) {
        if (reversed) {
            direction = Utility.getOppositeDirection(direction);
        }
        inputQueue.add(direction);

    }

    public Direction getPreviousDirection() {
        return previousDirection;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        if (invincible) {
            this.dead = false;
        } else {
            this.dead = dead;
            inputQueue.clear();
            direction = Direction.Static;
            if (pickupSlot != null) pickupSlot.done();
            listener.newMessage(new PlayerMessage(PlayerMessage.Event.Crashed, this));
        }
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        trail.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
        trail.setBorderColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
    }

    public Trail getTrail() {
        return trail;
    }

    public Pickup getPickupSlot() {
        return pickupSlot;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String toString() {
        return "Player{" + "id=" + id + ", name='" + name + '\'' + ", color=" + color + ", x=" + x + ", y=" + y
                + ", width=" + width + ", height=" + height + ", speed=" + speed + ", previousDirection="
                + previousDirection + ", direction=" + direction + ", dead=" + dead + ", ready=" + ready
                + ", invincible=" + invincible + ", pickupSlot=" + pickupSlot + ", trail=" + trail + '}';
    }
}
