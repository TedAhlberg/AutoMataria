package gameclient;

import gameobjects.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Johannes Blüml
 */
public class GamePanel extends JComponent {
    private Thread gameLoopThread;
    private boolean gameLoopRunning;

    private CopyOnWriteArraySet<GameObject> gameUpdatesQueue = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<GameObject> gameObjects = new CopyOnWriteArraySet<>();
    private Player player;
    private double scale = 1.0;
    private BufferedImage background, paintBuffer;
    private Color backgroundColor = Color.BLACK;
    private long timeBetweenRenders;
    private int fps, frameCounter, playerMovementPerSecond, interpolation;
    private boolean interpolateMovement;

    public void updateGameObjects(Collection<GameObject> updatedGameObjects) {
        if (gameObjects.size() == 0) {
            gameObjects.addAll(updatedGameObjects);
        } else {
            gameUpdatesQueue.clear();
            gameUpdatesQueue.addAll(updatedGameObjects);
            gameObjects.addAll(updatedGameObjects);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void start(double scale, int framesPerSecond, int playerMovementPerSecond) {
        this.playerMovementPerSecond = playerMovementPerSecond;
        this.scale = scale;
        timeBetweenRenders = (1000 / framesPerSecond) * 1000000;
        gameLoopRunning = true;
        gameLoopThread = new Thread(() -> gameLoop());
        gameLoopThread.start();
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
            interpolation = (int) Math.ceil(ratio * (playerMovementPerSecond / (1000000000.0 / timeSinceLastRender)));
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

    public void stop() {
        gameLoopRunning = false;
        gameLoopThread = null;
        background = null;
        paintBuffer = null;
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawBackground(g2);
        drawPaintBuffer(g2);
        drawDebugInfo(g2);
        g2.scale(scale, scale);

        if (gameObjects == null || gameUpdatesQueue.size() == 0) return;
        Collection<GameObject> updatedGameObjects = new HashSet<>(gameUpdatesQueue);

        for (GameObject target : updatedGameObjects) {
            if (target instanceof Trail || target instanceof Wall) {
                // Trail cant be interpolated so render the last one from server
                target.render(g2);
                continue;
            }
            for (GameObject current : gameObjects) {
                if (current.equals(target)) {
                    if (interpolateMovement) {
                        interpolate(current, target, interpolation);
                        current.render(g2);
                    } else {
                        target.render(g2);
                    }
                    if (target.equals(player)) {
                        player = (Player) target;
                    }
                }
            }
        }
        g2.dispose();
        Toolkit.getDefaultToolkit().sync();
        frameCounter += 1;
    }

    private void drawDebugInfo(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Orbitron", Font.PLAIN, 25));
        if (interpolateMovement) {
            g2.drawString(fps + " FPS | Interpolation: " + interpolation, 40, 60);
        } else {
            g2.drawString(fps + " FPS | Press I to enable movement interpolation.", 40, 60);
        }
    }

    private void interpolate(GameObject current, GameObject target, int interp) {
        current.setX(approach(current.getX(), target.getX(), interp));
        current.setY(approach(current.getY(), target.getY(), interp));
    }

    private int approach(int current, int target, int delta) {
        int difference = target - current;
        if (difference > delta) return current + delta;
        if (difference < -delta) return current - delta;
        return target;
    }

    public void setBackground(String filePath) {
        try {
            this.background = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
