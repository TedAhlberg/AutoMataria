package gameclient.interfaces.gamescreen;

import common.GameState;
import common.Utility;
import common.messages.TrailState;
import gameclient.Game;
import gameclient.Resources;
import gameobjects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

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
    private final CopyOnWriteArraySet<GameObject> gameObjects = new CopyOnWriteArraySet<>();
    private Interpolation interpolation = new Interpolation();
    private Thread gameLoopThread;
    private boolean gameLoopRunning;
    private BufferedImage background, gridBuffer;
    private GameState gameState = GameState.Warmup;
    private Color backgroundColor = Color.DARK_GRAY;
    private double scale = 1.0;
    private long timeBetweenRenders;
    private int fps, frameCounter;
    private boolean interpolateMovement = true, showFPS = false;
    private GamePanelText gamePanelText = new GamePanelText();

    /**
     * Starts the game loop with the provided maximum FPS
     *
     * @param maxFPS Maximum frames per second to render each second
     */
    public void start(int maxFPS) {
        timeBetweenRenders = (1000 / maxFPS) * 1000000;
        gameLoopRunning = true;
        gameLoopThread = new Thread(() -> gameLoop(), "ClientGameLoop");
        gameLoopThread.setPriority(Thread.MAX_PRIORITY - 1);
        gameLoopThread.start();
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
        for (GameObject updated : updatedGameObjects) {
            gameObjects.remove(updated);
            gameObjects.add(updated);
            if (updated instanceof Player && interpolateMovement) {
                interpolation.addTarget((Player) updated);
            }
        }
    }

    public void updateGameObjectStates(Collection<TrailState> trailStates) {
        for (TrailState trailState : trailStates) {
            if (trailState.trail != null) {
                gameObjects.add(trailState.trail);
            }
        }
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Trail) {
                Trail trail = (Trail) gameObject;
                for (TrailState trailState : trailStates) {
                    if (trailState.id == trail.getId()) {
                        trail.setColor(trailState.color);
                        trail.setBorderColor(trailState.borderColor);
                        trail.addTrailPoints(trailState.trailPoints);
                        System.out.println("TRAILPOINTS_SIZE=" + trailState.trailPoints.size());
                    }
                }
            }
        }
    }

    /**
     * Changes the grid that is displayed above the background
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
            repaint();

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
     * Renders the game to this component
     * This method is called in the gameLoop() method with paintImmediately()
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawBackground(g2);
        drawGridBuffer(g2);

        g2.scale(scale, scale);

        // Extract players so we can render them on top
        synchronized (lock) {
            ArrayList<Player> players = new ArrayList<>();
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof Player) {
                    players.add((Player) gameObject);
                } else {
                    gameObject.render(g2);
                }
            }
            for (Player player : players) {
                if (interpolateMovement) {
                    interpolation.interpolate(player);
                }
                player.render(g2);
            }
        }

        switch (gameState) {
            case Countdown:
                gamePanelText.drawText(g2, "GAME IS ABOUT TO BEGIN\n\nGET READY", GamePanelText.FontSize.Large, GamePanelText.Location.Center);
                break;
            case GameOver:
                gamePanelText.drawText(g2, "GAME OVER", GamePanelText.FontSize.Large, GamePanelText.Location.Center);
                break;
            case RoundOver:
                gamePanelText.drawText(g2, "ROUND OVER", GamePanelText.FontSize.Large, GamePanelText.Location.Center);
                break;
            case Warmup:
                gamePanelText.drawText(g2, "WARMUP ROUND\n\nWAITING FOR PLAYERS TO READY UP", GamePanelText.FontSize.Small, GamePanelText.Location.Center);
                if (showFPS) gamePanelText.drawTopLeftText(g2, fps + " FPS");
                break;
        }

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

    public void toggleFPS() {
        showFPS = !showFPS;
    }

    public void setServerTickRate(int tickRate) {
        interpolation.setTickRate(tickRate);
    }
}
