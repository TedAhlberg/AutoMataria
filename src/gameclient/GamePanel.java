package gameclient;

import gameobjects.GameObject;
import gameobjects.Player;
import gameserver.GameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Johannes Blüml
 */
public class GamePanel extends JComponent {
    private final ConcurrentHashMap<Integer, Point> currentPositions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Point> targetPositions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, GameObject> gameObjects = new ConcurrentHashMap<>();
    private Thread gameLoopThread;
    private boolean gameLoopRunning;
    private Player player;
    private double scale = 1.0;
    private BufferedImage background, paintBuffer;
    private Color backgroundColor = Color.BLACK;
    private long timeBetweenRenders;
    private int fps, frameCounter;
    private boolean interpolateMovement = true, showDebugInfo = true;
    private Function<Integer, Integer> calculateInterpolation = x -> 0;
    private GameState gameState = GameState.Warmup;
    private double playerReadyPercentage;

    public void start(double scale, int framesPerSecond) {
        this.scale = scale;
        timeBetweenRenders = (1000 / framesPerSecond) * 1000000;
        gameLoopRunning = true;
        gameLoopThread = new Thread(() -> gameLoop());
        gameLoopThread.start();
        gameLoopThread.setPriority(Thread.MAX_PRIORITY);
    }

    public void stop() {
        gameLoopRunning = false;
        gameLoopThread = null;
        background = null;
        paintBuffer = null;
    }

    public void updateGameObjects(Collection<GameObject> updatedGameObjects) {
        for (GameObject updated : updatedGameObjects) {
            gameObjects.put(updated.getId(), updated);
            if (interpolateMovement) {
                targetPositions.put(updated.getId(), new Point(updated.getX(), updated.getY()));
            }
            if (updated.equals(player)) {
                player = (Player) updated;
            }
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setBackground(String filePath) {
        try {
            this.background = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGrid(Dimension gridSize) {
        if (gridSize != null) {
            int size = Math.min(getWidth(), getHeight());
            Graphics2D g2 = getPaintBuffer();
            g2.setPaint(new Color(1, 1, 1, 0.05f));
            int spaceWidth = size / gridSize.width;
            int spaceHeight = size / gridSize.height;
            for (int i = 0; i <= spaceWidth * gridSize.width; i += spaceWidth) {
                g2.drawLine(i, 0, i, size);
            }
            for (int i = 0; i <= spaceHeight * gridSize.height; i += spaceHeight) {
                g2.drawLine(0, i, size, i);
            }
            g2.dispose();
        }
    }

    public void toggleInterpolation() {
        interpolateMovement = !interpolateMovement;
    }

    public void toggleDebugInfo() {
        showDebugInfo = !showDebugInfo;
    }

    private void gameLoop() {
        long previousTime = System.nanoTime();
        int lastSecond = (int) (previousTime / 1000000000);
        while (gameLoopRunning) {
            long nowTime = System.nanoTime();
            long timeSinceLastRender = nowTime - previousTime;
            previousTime = nowTime;

            // CALCULATE INTERPOLATION
            long ratio = timeSinceLastRender / timeBetweenRenders;
            calculateInterpolation = playerMovementPerSecond -> (int) Math.ceil(ratio * (playerMovementPerSecond / (1000000000.0 / timeSinceLastRender)));
            // RERENDER THE GAME
            paintImmediately(0, 0, getWidth(), getHeight());

            // UPDATE FPS EACH SECOND
            int thisSecond = (int) (previousTime / 1000000000);
            if (thisSecond > lastSecond) {
                fps = frameCounter;
                frameCounter = 0;
                lastSecond = thisSecond;
            }

            // WAIT BEFORE CONTINUING WITH THE GAMELOOP
            while (nowTime - previousTime < timeBetweenRenders) {
                Thread.yield();
                try { Thread.sleep(1); } catch (InterruptedException e) {}
                nowTime = System.nanoTime();
            }
        }
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawBackground(g2);
        drawPaintBuffer(g2);
        if (showDebugInfo) drawDebugInfo(g2);
        g2.scale(scale, scale);

        if (gameObjects.size() == 0) return;
        for (GameObject gameObject : gameObjects.values()) {
            if (gameObject instanceof Player && interpolateMovement) {
                interpolate(gameObject);
            }
            gameObject.render(g2);
        }
        g2.dispose();
        Toolkit.getDefaultToolkit().sync();
        frameCounter += 1;
    }

    private void interpolate(GameObject gameObject) {
        int id = gameObject.getId();
        int interpolation = calculateInterpolation.apply(((Player) gameObject).getSpeedPerSecond());

        // Get current position
        Point current = currentPositions.get(gameObject.getId());
        if (current == null) {
            // Set current position from the gameObject if there none available yet
            currentPositions.put(gameObject.getId(), new Point(gameObject.getX(), gameObject.getY()));
            current = currentPositions.get(gameObject.getId());
        }

        // Get target position
        Point target = targetPositions.get(gameObject.getId());
        if (target == null) {
            // Set current position from the gameObject if there is none available yet
            targetPositions.put(gameObject.getId(), new Point(gameObject.getX(), gameObject.getY()));
            target = targetPositions.get(gameObject.getId());
        }

        // Interpolate current position towards target position
        current.setLocation(approach(current.x, target.x, interpolation), approach(current.y, target.y, interpolation));
        gameObject.setX(current.x);
        gameObject.setY(current.y);
    }

    private int approach(int current, int target, int delta) {
        int difference = target - current;
        if (difference > delta) return current + delta;
        if (difference < -delta) return current - delta;
        return target;
    }

    private void drawDebugInfo(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 200));
        g2.setFont(new Font("Orbitron", Font.PLAIN, 16));

        String state = "STATE: " + gameState.toString().toUpperCase() + " ";
        if (gameState == GameState.Warmup) {
            state += (playerReadyPercentage * 100) + "% READY";
        }
        g2.drawString(fps + " FPS | " + state, 40, 60);

        String keyHelp = "C=TOGGLE COLOR | F1=TOGGLE HELP";
        keyHelp += (interpolateMovement) ? " | I=INTERP OFF" : " | I=INTERP ON";
        keyHelp += (player != null && player.isReady()) ? " | R=UNREADY" : " | R=READY";
        g2.drawString("KEYS " + keyHelp, 40, 90);
    }

    private void drawBackground(Graphics2D g2) {
        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void drawPaintBuffer(Graphics2D g2) {
        if (paintBuffer != null) g2.drawImage(paintBuffer, 0, 0, getWidth(), getHeight(), null);
    }

    private Graphics2D getPaintBuffer() {
        if (paintBuffer == null) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            GraphicsConfiguration config = device.getDefaultConfiguration();
            paintBuffer = config.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
        }
        return (Graphics2D) paintBuffer.getGraphics();
    }

    public void setGameState(GameState state) {
        gameState = state;
    }

    public void setReadyPlayers(double readyPercentage) {
        playerReadyPercentage = readyPercentage;
    }
}
