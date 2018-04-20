package gameobjects;

import common.*;
import gameclient.Game;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Johannes Blüml
 */
public class Player extends GameObject {
    private static final long serialVersionUID = 2;
    private final ConcurrentLinkedQueue<Direction> inputQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<GameObject> gameObjects;
    private final GameMap currentMap;
    private final String name;
    private final Trail trail;
    private Color color;
    private boolean dead, ready, invincible;
    private Direction previousDirection;
    private Pickup pickUpSlot;

    public Player(String name, ConcurrentLinkedQueue<GameObject> gameObjects, GameMap currentMap) {
        this.name = name;
        this.gameObjects = gameObjects;
        this.currentMap = currentMap;
        invincible = true;
        color = Color.LIGHT_GRAY;
        width = Game.GRID_PIXEL_SIZE;
        height = Game.GRID_PIXEL_SIZE;
        previousDirection = direction;
        trail = new Trail(this);
        trail.setId(ID.getNext());
    }

    public void render(Graphics2D g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(color.darker());
        Font font = new Font("Orbitron", Font.BOLD, 100);
        g.setFont(font);
        String displayName = name.toUpperCase();
        FontMetrics fontMetrics = g.getFontMetrics(font);
        int stringWidth = fontMetrics.stringWidth(displayName);
        if (dead) displayName += " (DEAD)";
        g.drawString(displayName, x + (Game.GRID_PIXEL_SIZE / 2) - (stringWidth / 2), y - 50);
    }

    public void tick() {
        if (pickUpSlot != null) {
            pickUpSlot.tick();
        }
        if (dead) return;

        updateDirection();
        move(speed);

        checkCollisions();
    }

    private boolean teleportIfOutsideMap() {
        int width = currentMap.getGrid().width * Game.GRID_PIXEL_SIZE;
        int height = currentMap.getGrid().height * Game.GRID_PIXEL_SIZE;
        if (x < 0) {
            x = width;
            return true;
        } else if (x > width) {
            x = 0;
            return true;
        } else if (y < 0) {
            y = height;
            return true;
        } else if (y > height) {
            y = 0;
            return true;
        }
        return false;
    }

    private void updateDirection() {
        previousDirection = direction;
        if (inputQueue.isEmpty()) return;

        while (inputQueue.peek() == direction || inputQueue.size() > 2) {
            inputQueue.remove();
        }

        if (inputQueue.isEmpty()) return;

        switch (direction) {
            case Up:
                for (int i = 0; i < speed; i++) {
                    if ((y - i) % Game.GRID_PIXEL_SIZE == 0) {
                        move(i);
                        previousDirection = direction;
                        direction = inputQueue.remove();
                        break;
                    }
                }
                break;
            case Down:
                for (int i = 0; i < speed; i++) {
                    if ((y + i) % Game.GRID_PIXEL_SIZE == 0) {
                        move(i);
                        previousDirection = direction;
                        direction = inputQueue.remove();
                        break;
                    }
                }
                break;
            case Left:
                for (int i = 0; i < speed; i++) {
                    if ((x - i) % Game.GRID_PIXEL_SIZE == 0) {
                        move(i);
                        previousDirection = direction;
                        direction = inputQueue.remove();
                        break;
                    }
                }
                break;
            case Right:
                for (int i = 0; i < speed; i++) {
                    if ((x + i) % Game.GRID_PIXEL_SIZE == 0) {
                        move(i);
                        previousDirection = direction;
                        direction = inputQueue.remove();
                        break;
                    }
                }
                break;
            default:
                previousDirection = direction;
                direction = inputQueue.remove();
        }

    }

    private void move(int amount) {
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
        if (!hasTeleported) {
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
                } else if (gameObject instanceof InstantPickup) {
                    ((InstantPickup) gameObject).use(this, gameObjects);
                    System.out.println("Player " + name + "used pickup " + gameObject);
                } else if (gameObject instanceof Pickup) {
                    ((Pickup) gameObject).take(this);
                    System.out.println("Player " + name + "picked up " + gameObject);
                }
            }
        }
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void usePickUp() {
        if (pickUpSlot != null) {
            pickUpSlot.use(this, gameObjects);
        }
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public void setNextDirection(Direction direction) {
        inputQueue.add(direction);
    }

    public void setPickUp(Pickup pickUp) {
        this.pickUpSlot = pickUp;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        if (invincible) {
            this.dead = false;
        } else {
            direction = Direction.Static;
            this.dead = dead;
            System.out.println(name + " HAS DIED");
        }
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Direction getPreviousDirection() {
        return previousDirection;
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

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", speed=" + speed +
                ", previousDirection=" + previousDirection +
                ", direction=" + direction +
                ", dead=" + dead +
                ", ready=" + ready +
                ", invincible=" + invincible +
                ", pickUpSlot=" + pickUpSlot +
                ", trail=" + trail +
                '}';
    }
}
