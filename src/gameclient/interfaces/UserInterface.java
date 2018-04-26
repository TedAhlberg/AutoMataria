package gameclient.interfaces;
import java.awt.*;
import javax.swing.JPanel;

import gameclient.Resources;
import gameclient.Window;
import test.MapEditorUI;

public class UserInterface extends JPanel {

    /**
     * @Erik Lundow
     */
    private CardLayout cardLayout = new CardLayout();

    public UserInterface(Dimension windowSize) {
        setLayout(cardLayout);
        setPreferredSize(windowSize);

        add(new StartScreen(this), "StartScreen");
        add(new SettingsScreen(this), "SettingsScreen");
        add(new HostServerScreen(this),"HostServerScreen");
        add(new MapEditorUI(windowSize).container, "MapEditorScreen");
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Resources.getImage("Stars.png"), 0, 0, getWidth(), getHeight(), null);
    }

    public static void main(String[] args) {
        Window window = new Window("Auto-Mataria");
        UserInterface userInterface = new UserInterface(window.getSize());
        window.setContentPane(userInterface);
        window.pack();
    }

    public void changeScreen(String screen) {
        cardLayout.show(this, screen);
    }
}
