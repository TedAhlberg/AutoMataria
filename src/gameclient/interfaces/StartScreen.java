package gameclient.interfaces;

import gameclient.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Henrik Olofsson & Erik Lundow
 */
public class StartScreen extends JPanel {
    private JPanel buttonsPanel = new JPanel(new GridBagLayout());
    private Font buttonFont = new Font("Orbitron", Font.BOLD, 30);

    private UserInterface userInterface;

    public StartScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        MusicManager.getInstance().gameTrack1();
        setLayout(new GridBagLayout());
        setOpaque(false);

        // Top spacing
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weighty = 5;
        add(new JPanel(), c);

        addButton("PLAY", "");
        addButton("HOST A GAME", "HostServerScreen");
        addButton("MAP EDITOR", "MapEditorScreen");
        addButton("HIGHSCORES", "");
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
        c.ipady = 40;
        add(buttonsPanel, c);

        // Bottom spacing
        c = new GridBagConstraints();
        c.gridy = 2;
        c.weighty = 1;
        add(new JPanel(), c);
    }

    private void addButton(String buttonText, String screenName) {
        addButton(buttonText, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                userInterface.changeScreen(screenName);
            }
        });
    }

    private void addButton(String buttonText, ActionListener actionListener) {
        Buttons button = new Buttons(buttonText);
        button.addActionListener(actionListener);
        button.setFont(buttonFont);

        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = 50;
        c.ipady = 50;
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        buttonsPanel.add(button, c);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Resources.getImage("Auto-Mataria.png"), 0, 0, getWidth(), getHeight(), null);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        UserInterface userInterface = new UserInterface(new Dimension(1200, 800));
//         frame.setMinimumSize(new Dimension(400, 400));
//         frame.setPreferredSize(new Dimension(930, 800));
        StartScreen sc = new StartScreen(userInterface);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(sc);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
