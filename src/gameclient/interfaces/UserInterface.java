package gameclient.interfaces;
import java.awt.CardLayout;
import javax.swing.JPanel;
import gameclient.Window;

public class UserInterface extends JPanel {

    private CardLayout cardLayout = new CardLayout();

    public UserInterface() {
        setLayout(cardLayout);

        add(new TestStartScreen(this), "StartScreen");
        add(new SettingsScreen(this), "SettingsScreen");

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
