package gameclient.interfaces;
import java.awt.*;
import javax.swing.JPanel;

import gameclient.BrowseServers;
import gameclient.Resources;
import gameclient.Window;
import test.MapEditorUI;

public class UserInterface extends JPanel {

    private static final long serialVersionUID = 1L;
    /**
     * @Erik Lundow
     */
    private CardLayout cardLayout = new CardLayout();
    private GameScreen gameScreen;

    public UserInterface(Dimension windowSize) {
        setLayout(cardLayout);
        setPreferredSize(windowSize);
        
        add(new StartScreen(this), "StartScreen");
        add(new SettingsScreen(this), "SettingsScreen");
        add(new HostServerScreen(this),"HostServerScreen");
        add(new MapEditorUI(windowSize).container, "MapEditorScreen");
        add(new BrowseServers(),"BrowseScreen");
        gameScreen= new GameScreen();
        add(gameScreen,"GameScreen");
        
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Resources.getImage("Stars.png"), 0, 0, getWidth(), getHeight(), null);
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "True");
        Window window = new Window("Auto-Mataria");
        UserInterface userInterface = new UserInterface(window.getSize());
        window.setContentPane(userInterface);
        window.pack();
    }

    public void changeScreen(String screen) {
        cardLayout.show(this, screen);
    }

    public void startGame(String ip, int port, String username) {
        gameScreen.connect(ip, port, username);
        cardLayout.show(this, "GameScreen");
    }
    

}
