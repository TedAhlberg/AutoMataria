package gameclient.interfaces.gamescreen;

import common.*;
import common.messages.GameServerUpdate;
import gameclient.Resources;
import gameobjects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

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
    private final HashSet<GameObject> gameObjects = new HashSet<>();
    private Interpolation interpolation = new Interpolation();
    private Thread gameLoopThread;
    private boolean gameLoopRunning;
    private volatile boolean isRendering;
    private BufferedImage background;
    private String backgroundImage;
    private GameState gameState = GameState.Warmup;
    private Color backgroundColor = Color.DARK_GRAY;
    private double scale = 1.0;
    private long timeBetweenRenders;
    private int fps, frameCounter;
    private boolean interpolateMovement = true, showFPS = false;
    private GamePanelText gamePanelText = new GamePanelText();

    public GamePanel() {
        setIgnoreRepaint(true);
        setDoubleBuffered(true);
    }

    /**
     * Starts the game loop with the provided maximum FPS
     *
     * @param maxFPS Maximum frames per second to render each second
     */
    public void start(int maxFPS) {
        timeBetweenRenders = Math.round((1000.0 / maxFPS) * 1000000.0);
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

    public void updateGameState(GameServerUpdate message) {
        synchronized (lock) {
            gameObjects.removeIf(gameObject ->
                    message.updated.contains(gameObject) || !message.existingObjects.contains(gameObject.getId()));

            gameObjects.addAll(message.added);

            for (GameObject updated : message.updated) {
                gameObjects.add(updated);
                if (updated instanceof Player && interpolateMovement) {
                    interpolation.addTarget((Player) updated);
                }
            }

            message.wallStates.forEach((id, wallState) -> {
                Wall wall = (Wall) Utility.getById(id, gameObjects);
                if (wall == null) return;
                wall.setColor(wallState.color);
                wall.setBorderColor(wallState.borderColor);
                wall.addGridPoints(wallState.addedPoints);
                if (!wallState.removedPoints.isEmpty()) {
                    wall.removeGridPoints(wallState.removedPoints);
                }
                wall.setDirection(wallState.direction);
            });
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

        background = Utility.createCompatibleImage(scaledSize, Transparency.OPAQUE);
        Graphics2D g2 = (Graphics2D) background.getGraphics();
        g2.drawImage(Resources.getImage(backgroundImage), 0, 0, scaledSize.width, scaledSize.height, null);
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
                interpolation.setCurrentDeltaTime(timeSinceLastRender / 1000000.0);
            }

            // Render the game to the panel
            isRendering = true;
            paintImmediately(new Rectangle(getSize()));

            // Update FPS counter each second
            int thisSecond = (int) (previousTime / 1000000000);
            if (thisSecond > lastSecond) {
                fps = frameCounter;
                frameCounter = 0;
                lastSecond = thisSecond;
            }

            // Wait until timeBetweenRenders nanoseconds have elapsed since the render began
            while (isRendering || nowTime - previousTime < timeBetweenRenders) {
                Thread.yield();
                nowTime = System.nanoTime();
            }
        }
    }

    /**
     * Renders the game to this component
     * This method is called in the gameLoop() method with repaint()
     */
    protected void paintComponent(Graphics g) {
        isRendering = true;
        Graphics2D g2 = (Graphics2D) g;
        drawBackground(g2);

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
        isRendering = false;
    }

    /**
     * Draws the Background BufferedImage or just black color if no image is set
     *
     * @param g2 Graphics2D object to draw on
     */
    private void drawBackground(Graphics2D g2) {
        if (background != null) {
            g2.drawImage(background, 0, 0, background.getWidth(), background.getHeight(), null);
        } else {
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public void setGameState(GameState state) {
        gameState = state;
    }

    public void setBackground(String file) {
        backgroundImage = file;
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
