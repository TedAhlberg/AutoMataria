package gameclient.interfaces;

import gameclient.Resources;
import gameclient.sound.MusicManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Henrik Olofsson & Erik Lundow
 */
public class StartScreen extends JPanel implements UserInterfaceScreen {
    private final Font buttonFont = Resources.getInstance().getDefaultFont().deriveFont(20f);
    private JPanel buttonsPanel;

    private UserInterface userInterface;

    public StartScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        MusicManager.getInstance().menuTrack();
        setLayout(new GridBagLayout());
        setOpaque(false);
    }

    private void createLayout() {
        // Top spacing
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weighty = 6;
        add(new JComponent() {}, c);

        buttonsPanel = new JPanel(new GridBagLayout());

        if (userInterface.getGameScreen().isConnectedToServer()) {
            addButton("BACK TO GAME", "GameScreen");
        }

        addButton("PLAY", "BrowseScreen");
        addButton("MAP EDITOR", "MapEditorScreen");
        addButton("HIGHSCORES", "HighScoreScreen");
        addButton("SETTINGS", "SettingsScreen");
        addButton("EXIT", new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 1;
        c.weightx = 1;
        c.ipady = 20;
        add(buttonsPanel, c);

        // Bottom spacing
        c = new GridBagConstraints();
        c.gridy = 2;
        c.weighty = 1;
        add(new JComponent() {}, c);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        StartScreen sc = new StartScreen(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(sc);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addButton(String buttonText, String screenName) {
        addButton(buttonText, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                userInterface.changeScreen(screenName);
            }
        });
    }

    private void addButton(String buttonText, ActionListener actionListener) {
        AMButton button = new AMButton(buttonText);
        button.addActionListener(actionListener);
        button.setFont(buttonFont);

        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        buttonsPanel.add(button, c);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Resources.getImage("Auto-Mataria.png"), 0, 0, getWidth(), getHeight(), null);
    }

    public void onScreenActive() {
        removeAll();
        createLayout();
        revalidate();
    }

    public void onScreenInactive() {
    }
}
