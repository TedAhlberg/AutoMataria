package gameclient.interfaces;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gameclient.Buttons;
import gameclient.Resources;
import gameclient.SoundFx;

/**
 * @author Henrik Olofsson & Erik Lundow
 */
public class TestStartScreen extends JPanel implements ActionListener {

    private BufferedImage bfImage;
    private String playFilenamePressed = "Play_Pressed.png";
    private String playFilenameUnpressed = "Play_Unpressed.png";
    private String browseFilenamePressed = "Browse_Pressed.png";
    private String browseFilenameUnpressed = "Browse_Unpressed.png";
    private String createFilenamePressed = "Create_Pressed.png";
    private String createFilenameUnpressed = "Create_Unpressed.png";
    private String settingsFilenamePressed = "Settings_Pressed.png";
    private String settingsFilenameUnpressed = "Settings_Unpressed.png";
    private String exitFilenamePressed = "Exit_Pressed.png";
    private String exitFilenameUnpressed = "Exit_Unpressed.png";

    private String highScoresFilenamePressed = "Highscore_Pressed.png";
    private String highScoresFilenameUnpressed = "Highscore_Unpressed.png";
    private Buttons btnPlay = new Buttons(playFilenamePressed, playFilenameUnpressed);
    private Buttons btnBrowse = new Buttons(browseFilenamePressed, browseFilenameUnpressed);
    private Buttons btnCreate = new Buttons(createFilenamePressed, createFilenameUnpressed);
    private Buttons btnSettings = new Buttons(settingsFilenamePressed, settingsFilenameUnpressed);
    private Buttons btnHighScore = new Buttons(highScoresFilenamePressed, highScoresFilenameUnpressed);
    private Buttons btnExit = new Buttons(exitFilenamePressed, exitFilenameUnpressed);
    private UserInterface userInterface;
    private JPanel pnlGrid = new JPanel(new GridLayout(1, 6, 0, 50));

    public TestStartScreen(UserInterface userInterface) {
        this.userInterface = userInterface;

        bfImage = Resources.getImage(("Auto-Mataria.png"));

        btnPlay.setMinimumSize(new Dimension(120, 120));
        btnPlay.setPreferredSize(new Dimension(120, 120));
        // add(btnPlay);
        pnlGrid.add(btnPlay);

        btnBrowse.setMinimumSize(new Dimension(120, 120));
        btnBrowse.setPreferredSize(new Dimension(120, 120));
        // add(btnBrowse);
        pnlGrid.add(btnBrowse);

        btnCreate.setMinimumSize(new Dimension(120, 120));
        btnCreate.setPreferredSize(new Dimension(120, 120));
        // add(btnCreate);
        pnlGrid.add(btnCreate);

        btnSettings.setMinimumSize(new Dimension(120, 120));
        btnSettings.setPreferredSize(new Dimension(120, 120));
        // add(btnSettings);
        pnlGrid.add(btnSettings);

        btnHighScore.setMinimumSize(new Dimension(120, 120));
        btnHighScore.setPreferredSize(new Dimension(120, 120));
        pnlGrid.add(btnHighScore);
        
        btnExit.setMinimumSize(new Dimension(120, 120));
        btnExit.setMaximumSize(new Dimension(120, 120));
        pnlGrid.add(btnExit);
        // add(btnHighScore);
        pnlGrid.setOpaque(false);

        add(pnlGrid);

        addListeners();

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bfImage, 0, 0, getWidth(), getHeight(), null);
        pnlGrid.setLocation(getWidth() / 6, getHeight() - getHeight() / 4);

    }

    public static void main(String[] args) {
         JFrame frame = new JFrame();
        UserInterface userInterface = new UserInterface();
//         frame.setMinimumSize(new Dimension(400, 400));
//         frame.setPreferredSize(new Dimension(930, 800));
         TestStartScreen sc = new TestStartScreen(userInterface);
         frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
         frame.add(sc);
         frame.pack();
         frame.setVisible(true);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addListeners() {
        btnSettings.addActionListener(this);
        btnExit.addActionListener(this);
        btnCreate.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSettings) {
            userInterface.changeScreen("SettingsScreen");
            SoundFx.getInstance().menuSelect();
        }
        
        if(e.getSource()==btnCreate) {
            userInterface.changeScreen("ServerScreen");
            SoundFx.getInstance().menuSelect();
        }
        
        if(e.getSource()==btnExit) {
            SoundFx.getInstance().menuSelect();
            System.exit(0);
        }

    }
}
