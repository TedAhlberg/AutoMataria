package gameclient;

import common.GameState;
import common.Utility;
import gameobjects.GameObject;
import gameobjects.Player;

import javax.swing.*;
import java.awt.*;
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
    private GameState gameState = GameState.Warmup;
    private Color backgroundColor = Color.DARK_GRAY;
    private double scale = 1.0;
    private long timeBetweenRenders;
    private int fps, frameCounter;
    private boolean interpolateMovement = true, showDebugInfo = true;
    private GamePanelText gamePanelText = new GamePanelText();

    /**
     * Starts the game loop with the provided maximum FPS
     *
     * @param maxFPS Maximum frames per second to render each second
     */
    public void start(int maxFPS) {
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
        if (gridSize == null || getWidth() == 0 || getHeight() == 0) return;
        int width = gridSize.width * Game.GRID_PIXEL_SIZE;
        int height = gridSize.height * Game.GRID_PIXEL_SIZE;

        // Calculate the scaling of GameObjects to the GamePanel size (Usually downscaling)
        scale = Math.min((double) getWidth() / width, (double) getHeight() / height);

        Dimension scaledSize = new Dimension((int) Math.round(width * scale), (int) Math.round(height * scale));

        gridBuffer = Utility.createCompatibleImage(scaledSize);
        Graphics2D g2 = (Graphics2D) gridBuffer.getGraphics();
        g2.scale(scale, scale);
        g2.setPaint(new Color(1, 1, 1, 0.05f));

        for (int i = Game.GRID_PIXEL_SIZE; i < width; i += Game.GRID_PIXEL_SIZE) {
            g2.drawLine(i, 0, i, height);
        }

        for (int i = Game.GRID_PIXEL_SIZE; i < height; i += Game.GRID_PIXEL_SIZE) {
            g2.drawLine(0, i, width, i);
        }

        g2.dispose();
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

        if (showDebugInfo) gamePanelText.drawDebugInfo(g2, gameState, fps);
        if (gameState == GameState.Countdown) gamePanelText.drawNewGameCountdown(g2);
        else if (gameState == GameState.GameOver) gamePanelText.drawGameOverInfo(g2);

        g2.dispose();
        Toolkit.getDefaultToolkit().sync();
        frameCounter += 1;
    }

    /**
     * Draws the Background BufferedImage or just black color if no image is set
     *
     * @param g2 Graphics2D object to draw on
     */
    private void drawBackground(Graphics2D g2) {
        if (background != null && gridBuffer != null) {
            g2.drawImage(background, 0, 0, gridBuffer.getWidth(), gridBuffer.getHeight(), null);
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
            g2.drawImage(gridBuffer, 0, 0, gridBuffer.getWidth(), gridBuffer.getHeight(), null);
        }
    }

    public void setGameState(GameState state) {
        gameState = state;
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
