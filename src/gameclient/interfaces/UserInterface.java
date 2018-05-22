package gameclient.interfaces;

import common.Action;
import common.Game;
import gameclient.Resources;
import gameclient.interfaces.gamescreen.GameScreen;
import gameclient.interfaces.highscorescreen.HighScoreScreen;
import gameclient.interfaces.hostserverscreen.HostServerScreen;
import gameclient.interfaces.mapeditorscreen.MapEditorScreen;
import gameclient.interfaces.serverbrowserscreen.BrowseServersScreen;
import gameclient.keyinput.KeyInput;
import gameclient.sound.MusicManager;
import gameclient.sound.MusicManager.Status;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class UserInterface extends JPanel {
    HashMap<String, UserInterfaceScreen> screens = new HashMap<>();
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
        MusicManager.setStatus(Status.InMenu);
        setLayout(cardLayout);
        setPreferredSize(windowSize);
        window = new Window(Game.TITLE, windowSize);
        window.setContentPane(this);
        window.setMode(Window.Mode.Windowed);

        settingsScreen = new SettingsScreen(this);
        gameScreen = new GameScreen(this);

        screens.put("StartScreen", new StartScreen(this));
        screens.put("BrowseScreen", new BrowseServersScreen(this));
        screens.put("SettingsScreen", settingsScreen);
        screens.put("HostServerScreen", new HostServerScreen(this));
        screens.put("HighScoreScreen", new HighScoreScreen(this));
        screens.put("ConnectScreen", new ConnectScreen(this));
        screens.put("MapEditorScreen", new MapEditorScreen(this));
        screens.put("GameScreen", gameScreen);

        screens.forEach((cardName, screenComponent) -> add((Component) screenComponent, cardName));

        // Show startscreen on startup
        changeScreen("StartScreen");

        // Listen to all keyboard buttons
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyInput(this));
    }

    public static void main(String[] args) {
        new Game();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Resources.getImage("Stars.png"), 0, 0, getWidth(), getHeight(), null);
    }

    public String getCurrentScreen() {
        return screenHistory.getLast();
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

    public void onKeyPress(Action action) {
        if (getCurrentScreen().equals("GameScreen") && gameScreen.isConnectedToServer()) {
            gameScreen.onKeyPress(action);
            return;
        }
        switch (action) {
            case InterfaceBack:
                changeToPreviousScreen();
                break;
        }
    }
}
