package gameclient.interfaces;

import gameclient.Resources;

import javax.swing.*;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class ConnectScreen extends JPanel implements UserInterfaceScreen {
    private AMButton joinGameButton, backButton;
    private JTextField portTextField, serverIPTextField;
    private UserInterface userInterface;

    public ConnectScreen(UserInterface userInterface) {
        this.userInterface = userInterface;
        setOpaque(false);
        createLayout();
        addListeners();
    }

    public static void main(String[] args) {
        Window window = new Window("ConnectScreen", new Dimension(1440, 800));
        window.setContentPane(new ConnectScreen(null));
        window.pack();
    }

    private void createLayout() {
        GridBagConstraints c;

        setLayout(new GridBagLayout());

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);

        JLabel headerLabel = new JLabel("CONNECT TO CUSTOM SERVER");
        headerLabel.setFont(Resources.getInstance().getTitleFont());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 10;
        c.insets = new Insets(0, 50, 0, 0);
        c.anchor = GridBagConstraints.WEST;
        topPanel.add(headerLabel, c);

        backButton = new AMButton("BACK");
        c = new GridBagConstraints();
        c.gridx = 2;
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(0, 0, 0, 50);
        c.anchor = GridBagConstraints.EAST;
        topPanel.add(backButton, c);


        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(topPanel, c);

        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(new JLabel("SERVER ADDRESS"), getFieldConstraints(0, 0));
        serverIPTextField = new JTextField("johannes.bluml.se");
        serverIPTextField.setMargin(new Insets(4, 6, 0, 0));
        panel.add(serverIPTextField, getFieldConstraints(1, 0));

        panel.add(new JLabel("SERVER PORT"), getFieldConstraints(2, 0));
        portTextField = new JTextField("32000");
        portTextField.setMargin(new Insets(4, 6, 0, 0));
        panel.add(portTextField, getFieldConstraints(3, 0));

        joinGameButton = new AMButton("JOIN SERVER");
        c = getFieldConstraints(4, 0);
        c.ipady = 10;
        panel.add(joinGameButton, c);

        c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 50;
        c.weightx = 1;
        add(panel, c);
    }

    private GridBagConstraints getFieldConstraints(int gridx, int gridy) {
        GridBagConstraints c = getFieldConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        return c;
    }

    private GridBagConstraints getFieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.ipadx = 10;
        c.ipady = 10;
        c.insets = new Insets(10, 50, 10, 50);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private void addListeners() {
        joinGameButton.addActionListener(e -> {
            userInterface.startGame(serverIPTextField.getText(), Integer.parseInt(portTextField.getText()));
        });
        backButton.addActionListener(e -> {
            userInterface.changeToPreviousScreen();
        });
    }

    public void onScreenActive() {

    }

    public void onScreenInactive() {

    }
}
