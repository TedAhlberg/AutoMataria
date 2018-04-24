package gameclient.interfaces;
import java.awt.CardLayout;
import javax.swing.JPanel;
import gameclient.Window;
import test.TestUI;

public class UserInterface extends JPanel {

    /**
     * @Erik Lundow
     */
    private CardLayout cardLayout = new CardLayout();

    public UserInterface() {
        setLayout(cardLayout);
        add(new TestStartScreen(this), "StartScreen");
        add(new SettingsScreen(this), "SettingsScreen");
        add(new TestUI().container,"ServerScreen");

    }

    public static void main(String[] args) {
        Window window = new Window("test");
        UserInterface userInterface = new UserInterface();
        userInterface.setPreferredSize(window.getSize());
        window.setContentPane(userInterface);
        window.pack();

    }

    public void changeScreen(String screen) {
        cardLayout.show(this, screen);

    }
}
