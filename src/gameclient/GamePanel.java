package gameclient;

import common.GameState;
import gameobjects.GameObject;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;

/**
 * GamePanel is a custom swing component that displays the game
 * GameObjects can be sent to it and they will be rendered on the component
 * <p>
 * Related requirements:
 * AF043, Spelplan
 * SF002, Visa spelet
 *
 * @author Johannes Blüml
 */
public class GamePanel extends JComponent {
    private final Object lock = new Object();
    private final LinkedList<GameObject> gameObjects = new LinkedList<>();
    private Interpolation interpolation = new Interpolation();
    private Thread gameLoopThread;
    private boolean gameLoopRunning;
    private BufferedImage background, gridBuffer;
    private Color backgroundColor = Color.BLACK;
    private GameState gameState = GameState.Warmup;
    private Dimension windowSize;
    private double scale = 1.0, playerReadyPercentage;
    private long timeBetweenRenders;
    private int fps, frameCounter, maxFPS;
    private boolean interpolateMovement = true, showDebugInfo = true;

    /**
     * The GamePanel will always try to match the provided size depending on the grid size of the game
     *
     * @param size Size of the GamePanel
     */
    public GamePanel(Dimension size) {
        this.windowSize = size;
        setSize(size);
    }

    /**
     * Starts the game loop with the provided maximum FPS
     *
     * @param maxFPS Maximum frames per second to render each second
     */
    public void start(int maxFPS) {
        this.maxFPS = maxFPS;
        timeBetweenRenders = (1000 / maxFPS) * 1000000;
        gameLoopRunning = true;
        gameLoopThread = new Thread(() -> gameLoop());
        gameLoopThread.start();
        gameLoopThread.setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Stops the game loop
     */
    public void stop() {
        gameLoopRunning = false;
        gameLoopThread = null;
        background = null;
        gridBuffer = null;
    }

    /**
     * Updates the gameobjects that are visible on the panel
     * Current objects are removed and the updated objects are added again
     *
     * @param updatedGameObjects Collection of updated GameObjects
     */
    public void updateGameObjects(Collection<GameObject> updatedGameObjects) {
        synchronized (lock) {
            gameObjects.clear();
            for (GameObject updated : updatedGameObjects) {
                gameObjects.add(updated);
                if (updated instanceof Player && interpolateMovement) {
                    interpolation.addTarget((Player) updated);
                }
            }
        }
    }

    /**
     * Changes the grid that is displayed above the background
     * Also note that this method will resize the panel in some cases
     * to display the grid evenly
     *
     * @param gridSize Amount of vertical and horizontal grid lines to draw
     */
    public void setGrid(Dimension gridSize) {
        if (gridSize != null) {
            int forceSize = Math.min(windowSize.width, windowSize.height);
            int gridWidth = forceSize / gridSize.width;
            int gridHeight = forceSize / gridSize.height;
            int width = gridWidth * gridSize.width;
            int height = gridHeight * gridSize.height;
            Dimension panelSize = new Dimension(width, height);
            setSize(panelSize);

            gridBuffer = createCompatibleImage(panelSize);
            Graphics2D g2 = (Graphics2D) gridBuffer.getGraphics();
            g2.setPaint(new Color(1, 1, 1, 0.05f));

            for (int i = 0; i <= width; i += gridWidth) {
                g2.drawLine(i, 0, i, height);
            }

            for (int i = 0; i <= height; i += gridHeight) {
                g2.drawLine(0, i, width, i);
            }

            g2.dispose();

            // Calculate the scaling of GameObjects to the GamePanel size (Usually downscaling)
            int gameObjectsWidth = gridSize.width * Game.GRID_PIXEL_SIZE;
            int gameObjectsHeight = gridSize.height * Game.GRID_PIXEL_SIZE;
            scale = Math.min((double) panelSize.width / gameObjectsWidth, (double) panelSize.height / gameObjectsHeight);
        }
    }

    /**
     * The main game loop manages when it is time to render a new frame to the panel
     */
    private void gameLoop() {
        long previousTime = System.nanoTime();
        int lastSecond = (int) (previousTime / 1000000000);
        while (gameLoopRunning) {
            long nowTime = System.nanoTime();
            long timeSinceLastRender = nowTime - previousTime;
            previousTime = nowTime;

            if (interpolateMovement) {
                interpolation.setCurrentDeltaTime(timeSinceLastRender / 1000000000.0);
            }

            // Render the game to the panel
            synchronized (lock) {
                paintImmediately(new Rectangle(getSize()));
            }

            // Update FPS counter each second
            int thisSecond = (int) (previousTime / 1000000000);
            if (thisSecond > lastSecond) {
                fps = frameCounter;
                frameCounter = 0;
                lastSecond = thisSecond;
            }

            // Wait until timeBetweenRenders nanoseconds have elapsed since the render began
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

    /**
     * Renders the game to the component
     * This method is called in the gameLoop() method with paintImmediately()
     *
     * @param g Graphics object of the panel
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawBackground(g2);
        drawGridBuffer(g2);

        g2.scale(scale, scale);

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player && interpolateMovement) {
                interpolation.interpolate((Player) gameObject);
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

    private void drawDebugInfo(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 200));
        g2.setFont(new Font("Orbitron", Font.PLAIN, 100));

        String state = "STATE: " + gameState.toString().toUpperCase() + " ";
        if (gameState == GameState.Warmup) {
            state += Math.round(playerReadyPercentage * 100) + "% READY";
            //state += (player != null && player.isReady()) ? " | YOU ARE READY" : " | PRESS R TO READY UP";
        }
        g2.drawString(fps + "/" + maxFPS + " FPS | " + state, 40, 120);

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

    /**
     * Draws the Background BufferedImage or just black color if no image is set
     *
     * @param g2 Graphics2D object to draw on
     */
    private void drawBackground(Graphics2D g2) {
        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Draws the BufferedImage that contains the grid created in setGrid() method
     *
     * @param g2 Graphics2D object to draw on
     */
    private void drawGridBuffer(Graphics2D g2) {
        if (gridBuffer != null) {
            g2.drawImage(gridBuffer, 0, 0, getWidth(), getHeight(), null);
        }
    }

    /**
     * Creates a compatible BufferedImage that can be used to paint on
     *
     * @param size Dimension of the BufferedImage that is created
     * @return A BufferedImage
     */
    private BufferedImage createCompatibleImage(Dimension size) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();
        return config.createCompatibleImage(size.width, size.height, Transparency.TRANSLUCENT);
    }

    public void setGameState(GameState state) {
        gameState = state;
    }

    public void setReadyPlayers(double readyPercentage) {
        playerReadyPercentage = readyPercentage;
    }

    public void setBackground(String file) {
        background = Resources.getImage(file);
    }

    public void toggleInterpolation() {
        interpolateMovement = !interpolateMovement;
    }

    public void toggleDebugInfo() {
        showDebugInfo = !showDebugInfo;
    }

    public void setServerTickRate(int tickRate) {
        interpolation.setTickRate(tickRate);
    }
}
