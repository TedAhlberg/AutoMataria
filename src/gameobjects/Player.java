package gameobjects;

import common.Direction;
import common.GameMap;
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
    private int speedPerSecond;
    private Pickup pickUpSlot;
    private int tickRate;

    public Player(String name, ConcurrentLinkedQueue<GameObject> gameObjects, GameMap currentMap) {
        this.name = name;
        this.invincible = true;
        this.gameObjects = gameObjects;
        this.currentMap = currentMap;
        this.color = Color.LIGHT_GRAY;
        this.width = Game.GRID_PIXEL_SIZE;
        this.height = Game.GRID_PIXEL_SIZE;
        this.previousDirection = direction;
        this.trail = new Trail(this);
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
        if (dead)
            displayName += " (DEAD)";
        g.drawString(displayName, x + (Game.GRID_PIXEL_SIZE / 2) - (stringWidth / 2), y - 50);
    }

    public void tick() {
        if (dead)
            return;

        Point previousPosition = new Point(x, y);

        updateDirection();
        move();
        boolean hasTeleported = teleportIfOutsideMap();
        if (!hasTeleported) {
            Point newPosition = new Point(x, y);
            trail.grow(previousPosition, newPosition);
        }

        if (!invincible)
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
        if (inputQueue.isEmpty())
            return;

        while (inputQueue.peek() == direction) {
            inputQueue.remove();
        }

        if (inputQueue.isEmpty())
            return;

        if (x % Game.GRID_PIXEL_SIZE == 0 && y % Game.GRID_PIXEL_SIZE == 0) {
            previousDirection = direction;
            direction = inputQueue.remove();
        }
    }

    private void move() {
        switch (direction) {
        case Up:
            y -= speed;
            break;
        case Down:
            y += speed;
            break;
        case Left:
            x -= speed;
            break;
        case Right:
            x += speed;
            break;
        }
    }

    private void checkCollisions() {
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                Player otherPlayer = (Player) gameObject;
                if (otherPlayer.equals(this)) continue;
                if (otherPlayer.getBounds().intersects(this.getBounds())) {
                    setDead(true);
                    System.out.println(name + " HAS CRASHED WITH " + otherPlayer.getName());
                }
            } else if (gameObject instanceof Wall) {
                if (((Wall) gameObject).intersects(this.getBounds())) {
                    setDead(true);
                    System.out.println(name + " HAS CRASHED INTO A WALL");
                }
            } else if(gameObject instanceof Pickup) {
                if (this.getBounds().intersects(gameObject.getBounds())) {
                    this.setPickUp((Pickup) gameObject);
                    gameObjects.remove(gameObject);
                }
            }   
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        direction = Direction.Static;
        this.dead = dead;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public void setDirection(Direction direction) {
        inputQueue.add(direction);
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

    public int getSpeedPerSecond() {
        return (1000 / tickRate) * speed;
    }

    public Trail getTrail() {
        return trail;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setPickUp(Pickup pickUp) {
        this.pickUpSlot = pickUp;
    }

    public void usePickUp() {
        if (pickUpSlot != null) {
            pickUpSlot.use(this);
            pickUpSlot = null;
            
        }
    }

    @Override
    public String toString() {
        return "Player{" + "name='" + name + '\'' + ", color=" + color + ", dead=" + dead + ", ready=" + ready
                + ", previousDirection=" + previousDirection + ", speedPerSecond=" + speedPerSecond + ", id=" + id
                + ", x=" + x + ", y=" + y + ", speed=" + speed + ", width=" + width + ", height=" + height
                + ", direction=" + direction + '}';
    }

    public void setTickrate(int tickRate) {
        this.tickRate = tickRate;
    }
}
