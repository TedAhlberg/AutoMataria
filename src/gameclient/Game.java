package gameclient;

import common.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Johannes Bluml
 */
public class Game extends JComponent {
    public static final String TITLE = "Auto-Mataria";
    private Rectangle screen;
    private CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<>();
    private CopyOnWriteArraySet<Point> paintedPositions = new CopyOnWriteArraySet<>();
    private double scale;
    private GameMap map;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    private GameServerConnection client;
    private BufferedImage background;
    private Audio backgroundMusic = new Audio("AM-trck1.mp3");
    private Timer reRenderTimer;

    public Game() {
        this("localhost", 32000, null);
    }
    public Game(String serverIP, int serverPort) {
        this(serverIP, serverPort, null);
    }
    public Game(String serverIP, int serverPort, Dimension windowSize) {
        String playerName = JOptionPane.showInputDialog("Enter your username:", "Username");
        Window window = new Window(TITLE, this);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            env.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/Orbitron Bold.ttf")));
        } catch (IOException | FontFormatException e) {
            System.out.println("Failed to load resources.");
            e.printStackTrace();
        }

        if (windowSize == null) {
            env.getDefaultScreenDevice().setFullScreenWindow(window);
            screen = env.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        } else {
            window.setPreferredSize(windowSize);
            window.setMinimumSize(windowSize);
            window.setMaximumSize(windowSize);
            window.pack();
            window.setVisible(true);
            screen = new Rectangle(windowSize);
        }

        client = new GameServerConnection(new GameServerListener() {
            public void onConnect() {
                System.out.println("Connected to server.");
                client.send(playerName);
            }
            public void onDisconnect() {
                System.out.println("Disconnected from server");
                if (reRenderTimer != null && reRenderTimer.isRunning()) {
                    reRenderTimer.stop();
                }
                backgroundMusic.stop();
                window.dispose();
            }
            public void onData(Object data) {
                if (data instanceof GameMap) {
                    changeGameMap((GameMap) data);
                    reRenderTimer = new Timer(16, e -> Game.this.repaint());
                    reRenderTimer.start();
                    backgroundMusic.play();
                } else if (data instanceof Collection) {
                    gameObjects = new CopyOnWriteArrayList<>();
                    gameObjects.addAll((Collection<GameObject>) data);
                }
            }
        });
        this.addKeyListener(new KeyInput(client));
        this.requestFocus();

        client.connect(serverIP, serverPort);
    }

    public static int clamp(int var, int min, int max) {
        if (var >= max) return max;
        if (var <= min) return min;
        return var;
    }

    private void changeGameMap(GameMap map) {
        this.map = map;
        try {
            background = ImageIO.read(new File(map.getBackground()));
        } catch (IOException e) {
            System.out.println("Failed to load resources.");
            e.printStackTrace();
            // TODO: Set a default background when failed to load.
        }

        this.scale = Math.min(screen.getWidth() / map.getWidth(), screen.getHeight() / map.getHeight());

        Graphics2D g = (Graphics2D) background.getGraphics();
        g.scale(this.scale, this.scale);
        g.setPaint(new Color(1, 1, 1, 0.2f));
        for (int i = 0; i <= map.getWidth(); i += map.getGridSize()) {
            g.drawLine(i, 0, i, map.getHeight());
        }
        for (int i = 0; i <= map.getHeight(); i += map.getGridSize()) {
            g.drawLine(0, i, map.getWidth(), i);
        }
        g.dispose();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background == null) return;

        Graphics2D g2 = (Graphics2D) g;
        Graphics2D gBackground = (Graphics2D) background.getGraphics();

        g2.drawImage(background, 0, 0, this);
        g2.scale(scale, scale);
        gBackground.scale(scale, scale);
        for (GameObject gameObject : gameObjects) {
            if (gameObject == null) continue;
            if (gameObject instanceof Trail) {
                ((Trail) gameObject).removeAll(paintedPositions);
                paintedPositions.addAll(((Trail) gameObject).getAll());
                gameObject.render(gBackground);
            } else if (gameObject instanceof Wall) {
                gameObject.render(gBackground);
            } else {
                gameObject.render(g2);
            }
        }

        toolkit.sync();
    }
}
