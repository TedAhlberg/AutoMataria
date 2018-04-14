package gameclient;

import common.GameState;
import gameobjects.GameObject;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @author Johannes Blüml
 */
public class GamePanel extends JComponent {
    private final ConcurrentHashMap<GameObject, Point> currentPositions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<GameObject, Point> targetPositions = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<GameObject> gameObjects = new CopyOnWriteArraySet<>();
    private Thread gameLoopThread;
    private boolean gameLoopRunning;
    private Player player;
    private double scale = 1.0;
    private BufferedImage background, gridBuffer;
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
        gridBuffer = null;
    }

    public void updateGameObjects(Collection<GameObject> updatedGameObjects) {
        gameObjects.clear();
        for (GameObject updated : updatedGameObjects) {
            gameObjects.add(updated);
            if (interpolateMovement) {
                targetPositions.put(updated, new Point(updated.getX(), updated.getY()));
            }
            if (updated.equals(player)) {
                player = (Player) updated;
            }
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setBackground(String file) {
        background = Resources.getImage(file);
    }

    public void setGrid(Dimension gridSize) {
        if (gridSize != null) {
            int size = Math.min(getWidth(), getHeight());
            Graphics2D g2 = createGridBuffer();
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
            calculateInterpolation = playerMovementPerSecond ->
                    (int) Math.ceil(ratio * (playerMovementPerSecond / (1000000000.0 / timeSinceLastRender)));
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
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
                nowTime = System.nanoTime();
            }
        }
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawBackground(g2);
        drawGridBuffer(g2);

        g2.scale(scale, scale);

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && interpolateMovement) {
                interpolate(gameObject);
            }
            gameObject.render(g2);
        }

        if (showDebugInfo) drawDebugInfo(g2);
        if (gameState == GameState.Countdown) drawNewGameCountdown(g2);
        if (gameState == GameState.GameOver) drawGameOverInfo(g2);

        g2.dispose();
        Toolkit.getDefaultToolkit().sync();
        frameCounter += 1;
    }

    private void interpolate(GameObject gameObject) {
        int interpolation = calculateInterpolation.apply(((Player) gameObject).getSpeedPerSecond());

        // Get current position
        Point current = currentPositions.get(gameObject);
        if (current == null) {
            // Set current position from the gameObject if there none available yet
            currentPositions.put(gameObject, new Point(gameObject.getX(), gameObject.getY()));
            current = currentPositions.get(gameObject);
        }

        // Get target position
        Point target = targetPositions.get(gameObject);

        {
            // Sets a limit for how far behind the current position can be from target
            int limit = 3 * Game.GRID_PIXEL_SIZE;
            int xDiff = target.x - current.x;
            int yDiff = target.y - current.y;
            if (xDiff > limit || xDiff < -limit || yDiff > limit || yDiff < -limit) {
                current.x = target.x;
                current.y = target.y;
            }
        }

        // Interpolate current position towards target position
        //System.out.println("Target: " + target + " Current: " + current);
        current.setLocation(approach(current.x, target.x, interpolation), approach(current.y, target.y, interpolation));
        gameObject.setX(current.x);
        gameObject.setY(current.y);
    }

    private int approach(int current, int target, int delta) {
        int difference = target - current;
        if (difference > delta)
            return current + delta;
        if (difference < -delta)
            return current - delta;
        return target;
    }

    private void drawDebugInfo(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 200));
        g2.setFont(new Font("Orbitron", Font.PLAIN, 100));

        String state = "STATE: " + gameState.toString().toUpperCase() + " ";
        if (gameState == GameState.Warmup) {
            state += Math.round(playerReadyPercentage * 100) + "% READY";
            state += (player != null && player.isReady()) ? " | YOU ARE READY" : " | PRESS R TO READY UP";
        }
        g2.drawString(fps + " FPS | " + state, 40, 120);

        String keyHelp = "TOGGLE COLOR : C | TOGGLE HELP : F1";
        keyHelp += (interpolateMovement) ? " | INTERP ON : I" : " | INTERP OFF : I";
        g2.drawString(keyHelp, 40, 240);
    }

    private void drawNewGameCountdown(Graphics2D g2) {
        String infoText = "GAME IS ABOUT TO BEGIN";
        String infoText2 = "GET READY";
        Font font = new Font("Orbitron", Font.BOLD, 200);
        g2.setFont(font);
        g2.setColor(new Color(255, 255, 255, 200));
        FontMetrics fontMetrics = g2.getFontMetrics();
        Rectangle2D infoTextBounds = fontMetrics.getStringBounds(infoText, g2);
        Rectangle2D infoText2Bounds = fontMetrics.getStringBounds(infoText2, g2);
        int width = g2.getClipBounds().width;
        int height = g2.getClipBounds().height;
        int infoTextWidth = (int) ((width / 2) - (infoTextBounds.getWidth() / 2));
        int infoTextHeight = (int) (((height / 2) - (infoTextBounds.getHeight() / 2)) - 100);
        int infoText2Width = (int) ((width / 2) - (infoText2Bounds.getWidth() / 2));
        int infoText2Height = (int) (((height / 2) - (infoText2Bounds.getHeight() / 2)) + 100);

        g2.drawString(infoText, infoTextWidth, infoTextHeight);
        g2.drawString(infoText2, infoText2Width, infoText2Height);
    }

    private void drawGameOverInfo(Graphics2D g2) {
        String infoText = "GAME OVER";
        Font font = new Font("Orbitron", Font.BOLD, 300);
        g2.setFont(font);
        g2.setColor(new Color(255, 255, 255, 200));
        FontMetrics fontMetrics = g2.getFontMetrics();
        Rectangle2D infoTextBounds = fontMetrics.getStringBounds(infoText, g2);
        int width = g2.getClipBounds().width;
        int height = g2.getClipBounds().height;
        int infoTextWidth = (int) ((width / 2) - (infoTextBounds.getWidth() / 2));
        int infoTextHeight = (int) (((height / 2) - (infoTextBounds.getHeight() / 2)));

        g2.drawString(infoText, infoTextWidth, infoTextHeight);
    }

    private void drawBackground(Graphics2D g2) {
        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void drawGridBuffer(Graphics2D g2) {
        if (gridBuffer != null)
            g2.drawImage(gridBuffer, 0, 0, getWidth(), getHeight(), null);
    }

    private Graphics2D createGridBuffer() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();
        gridBuffer = config.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
        return (Graphics2D) gridBuffer.getGraphics();
    }

    public void setGameState(GameState state) {
        gameState = state;
    }

    public void setReadyPlayers(double readyPercentage) {
        playerReadyPercentage = readyPercentage;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
