package gameclient.interfaces;

import gameclient.*;
import gameclient.Window;
import gameclient.keyinput.KeyInput;
import test.MapEditorUI;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class UserInterface extends JPanel {

    private static final long serialVersionUID = 1L;
    /**
     * @eriklundow
     */
    private Window window;
    private CardLayout cardLayout = new CardLayout();
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;
    private LinkedList<String> screenHistory = new LinkedList<>();

    public UserInterface() {
        this(null);
    }

    public UserInterface(Dimension windowSize) {

        window = new Window(Game.TITLE, windowSize);
        window.setContentPane(this);
        window.pack();

        setLayout(cardLayout);
        setPreferredSize(windowSize);

        add(new StartScreen(this), "StartScreen");
        settingsScreen = new SettingsScreen(this);
        add(new SettingsScreen(this), "SettingsScreen");
        add(new HostServerScreen(this), "HostServerScreen");
        add(new MapEditorUI(this).container, "MapEditorScreen");
        add(new BrowseServers(this), "BrowseScreen");
        add(new ConnectScreen(this), "ConnectScreen");
        gameScreen = new GameScreen(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyInput(gameScreen));
        add(gameScreen, "GameScreen");

        // Show startscreen on startup
        changeScreen("StartScreen");
    }

    public static void main(String[] args) {
        new Game();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Resources.getImage("Stars.png"), 0, 0, getWidth(), getHeight(), null);
    }

    public void changeScreen(String screen) {
        if (screen.equals("StartScreen")) {
            screenHistory.clear();
        }
        screenHistory.add(screen);
        cardLayout.show(this, screen);
    }

    public void changeToPreviousScreen() {
        screenHistory.removeLast();
        cardLayout.show(this, screenHistory.getLast());
    }

    public void startGame(String ip, int port) {
        gameScreen.connect(ip, port, settingsScreen.getUsername());
        changeScreen("GameScreen");
    }

    public void setWindowMode(Window.Mode windowMode) {
        window.setMode(windowMode);
    }
}
