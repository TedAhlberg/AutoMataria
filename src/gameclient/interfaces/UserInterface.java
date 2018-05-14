package gameclient.interfaces;

import gameclient.Game;
import gameclient.Resources;
import gameclient.interfaces.gamescreen.GameScreen;
import gameclient.interfaces.hostserverscreen.HostServerScreen;
import gameclient.interfaces.serverbrowserscreen.BrowseServersScreen;
import gameclient.keyinput.KeyInput;
import test.MapEditorUI;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class UserInterface extends JPanel {
    /**
     * @eriklundow
     */
    private Window window;
    private CardLayout cardLayout = new CardLayout();
    private GameScreen gameScreen;
    private SettingsScreen settingsScreen;
    private LinkedList<String> screenHistory = new LinkedList<>();
    HashMap<String, UserInterfaceScreen> screens = new HashMap<>();

    public UserInterface() {
        this(null);
    }

    public UserInterface(Dimension windowSize) {
        setLayout(cardLayout);
        setPreferredSize(windowSize);
        window = new Window(Game.TITLE, windowSize);
        window.setContentPane(this);
        window.setMode(Window.Mode.Windowed);

        settingsScreen = new SettingsScreen(this);
        gameScreen = new GameScreen(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyInput(gameScreen));

        screens.put("StartScreen", new StartScreen(this));
        screens.put("BrowseScreen", new BrowseServersScreen(this));
        screens.put("SettingsScreen", settingsScreen);
        screens.put("HostServerScreen", new HostServerScreen(this));
        screens.put("HighScoreScreen", new HighScoreScreen(this));
        screens.put("ConnectScreen", new ConnectScreen(this));
        screens.put("GameScreen", gameScreen);

        screens.forEach((cardName, screenComponent) -> add((Component) screenComponent, cardName));

        add(new MapEditorUI(this).container, "MapEditorScreen");

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
        String previousScreen = screenHistory.isEmpty() ? "StartScreen" : screenHistory.getLast();

        if (screen.equals("StartScreen")) {
            screenHistory.clear();
        }
        screenHistory.add(screen);
        cardLayout.show(this, screen);

        screens.get(previousScreen).onScreenInactive();
        screens.get(screen).onScreenActive();
    }

    public void changeToPreviousScreen() {
        if (screenHistory.size() <= 1) return;
        String previousScreen = screenHistory.removeLast();
        String screen = screenHistory.getLast();

        cardLayout.show(this, screen);

        screens.get(previousScreen).onScreenInactive();
        screens.get(screen).onScreenActive();
    }

    public void startGame(String ip, int port) {
        gameScreen.connect(ip, port);
        changeScreen("GameScreen");
    }

    public void setWindowMode(Window.Mode windowMode) {
        window.setMode(windowMode);
    }

    public SettingsScreen getSettingsScreen() {
        return settingsScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
}
