package test;

import common.Maps;
import gameclient.Game;
import gameclient.Window;
import gameclient.interfaces.UserInterface;
import gameserver.GameServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Johannes Blüml
 */
public class TestUI {
    public JPanel container;
    private JTextField tfClientIP, tfClientPort, tfServerPort;
    private JButton btnStartGame, btnStartServer, btnStopServer;
    private JCheckBox checkFullscreen;
    private JComboBox<String> cbWindowSize;
    private JLabel lblWindowSize;
    private JComboBox<String> serverProfileComboBox;
    private JSlider playerSpeedSlider;
    private JSlider updatesSlider;
    private JSlider tickRateSlider;
    private JComboBox<String> mapsComboBox;

    private GameServer server;

    private String clientIP = "127.0.0.1", gameMap;
    private int serverPort = 32000,
            tickRate = 100,
            ticksBetweenUpdates = 2,
            clientPort = 32000,
            clientWidth = 750,
            clientHeight = 750,
            playerSpeed = 50;
    private boolean fullscreen;

    public TestUI() {
        $$$setupUI$$$();
        container.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                SwingUtilities.invokeLater(() -> {
                    updateAllVariables();
                    updateAllGUIFields();
                });
            }
        });

        checkFullscreen.addChangeListener(e -> {
            fullscreen = checkFullscreen.isSelected();
            lblWindowSize.setEnabled(!fullscreen);
            cbWindowSize.setEnabled(!fullscreen);
        });

        btnStartGame.addActionListener(e -> {
            updateAllVariables();

            SwingUtilities.invokeLater(() -> {
                UserInterface userInterface;
                if (fullscreen) {
                    userInterface = new UserInterface();
                } else {
                    userInterface = new UserInterface(new Dimension(clientWidth, clientHeight));
                }
                userInterface.changeScreen("GameScreen");
                userInterface.startGame(clientIP, clientPort);
            });
        });

        mapsComboBox.addActionListener(e -> {
            if (server != null) {
                server.changeMap(Maps.getInstance().get((String) mapsComboBox.getSelectedItem()));
            }
        });

        btnStartServer.addActionListener(e -> {
            updateAllVariables();
            System.out.println(toString());
            if (server == null) {
                server = new GameServer("AM-test-server", serverPort, tickRate, ticksBetweenUpdates, playerSpeed, Maps.getInstance().get(gameMap));
                server.start();
            }
        });
        btnStopServer.addActionListener(e -> {
            if (server != null) {
                server.stop();
                server = null;
            }
        });

        serverProfileComboBox.addActionListener(e -> {
            String profile = (String) serverProfileComboBox.getSelectedItem();

            if (profile.equals("Custom")) {
                playerSpeedSlider.setEnabled(true);
                tickRateSlider.setEnabled(true);
                updatesSlider.setEnabled(true);
                return;
            }

            playerSpeedSlider.setEnabled(false);
            tickRateSlider.setEnabled(false);
            updatesSlider.setEnabled(false);

            switch (profile) {
                case "Low Performance":
                    playerSpeedSlider.setValue(75);
                    tickRateSlider.setValue(150);
                    updatesSlider.setValue(2);
                    break;
                case "High Performance":
                    playerSpeedSlider.setValue(15);
                    tickRateSlider.setValue(50);
                    updatesSlider.setValue(2);
                    break;
                case "Extreme Performance":
                    playerSpeedSlider.setValue(15);
                    tickRateSlider.setValue(50);
                    updatesSlider.setValue(1);
                    break;
                default:
                    playerSpeedSlider.setValue(50);
                    tickRateSlider.setValue(100);
                    updatesSlider.setValue(2);
            }
        });

        updateAllGUIFields();
        serverProfileComboBox.setSelectedItem("Normal (Default)");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TestUI2");
        frame.setContentPane(new TestUI().container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void updateAllGUIFields() {
        // CLIENT
        cbWindowSize.setSelectedItem(clientWidth + " " + clientHeight);
        checkFullscreen.setSelected(fullscreen);
        tfClientIP.setText(clientIP);
        tfClientPort.setText(Integer.toString(clientPort));
        // SERVER
        tfServerPort.setText(Integer.toString(serverPort));
        tickRateSlider.setValue(tickRate);
        updatesSlider.setValue(ticksBetweenUpdates);
        playerSpeedSlider.setValue(playerSpeed);
        if (gameMap != null) {
            mapsComboBox.setSelectedItem(gameMap);
        }
    }

    private void updateAllVariables() {
        try {
            // CLIENT
            String[] parts = cbWindowSize.getSelectedItem().toString().split("x");
            clientWidth = Integer.parseInt(parts[0]);
            clientHeight = Integer.parseInt(parts[1]);
            fullscreen = checkFullscreen.isSelected();
            clientIP = tfClientIP.getText();
            clientPort = Integer.parseInt(tfClientPort.getText());
            // SERVER
            serverPort = Integer.parseInt(tfServerPort.getText());
            tickRate = tickRateSlider.getValue();
            ticksBetweenUpdates = updatesSlider.getValue();
            playerSpeed = playerSpeedSlider.getValue();
            gameMap = (String) mapsComboBox.getSelectedItem();

        } catch (NumberFormatException error) {
            JOptionPane.showMessageDialog(null, "Please enter only numbers.");
        }
    }

    private void createUIComponents() {
        mapsComboBox = new JComboBox<>();
        for (String item : Maps.getInstance().getMapList()) {
            mapsComboBox.addItem(item);
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        container = new JPanel();
        container.setLayout(new GridBagLayout());
        tfServerPort = new JTextField();
        tfServerPort.setText("32000");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfServerPort, gbc);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Client Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Game IP");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label2, gbc);
        tfClientIP = new JTextField();
        tfClientIP.setText("127.0.0.1");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfClientIP, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        container.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        container.add(spacer2, gbc);
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.BOLD, -1, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Server Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Server Port");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label4, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        container.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 16;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 18;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer6, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer7, gbc);
        btnStopServer = new JButton();
        btnStopServer.setText("Stop Server");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 17;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(btnStopServer, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.ipadx = 10;
        container.add(spacer9, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Tick Rate (ms)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Ticks/Update");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label6, gbc);
        btnStartServer = new JButton();
        btnStartServer.setText("Start Server");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(btnStartServer, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Server Profile");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label7, gbc);
        serverProfileComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Custom");
        defaultComboBoxModel1.addElement("Low Performance");
        defaultComboBoxModel1.addElement("Normal (Default)");
        defaultComboBoxModel1.addElement("High Performance");
        defaultComboBoxModel1.addElement("Extreme Performance");
        serverProfileComboBox.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(serverProfileComboBox, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Player Speed");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label8, gbc);
        playerSpeedSlider.setEnabled(false);
        playerSpeedSlider.setMajorTickSpacing(25);
        playerSpeedSlider.setMinimum(0);
        playerSpeedSlider.setMinorTickSpacing(5);
        playerSpeedSlider.setPaintLabels(true);
        playerSpeedSlider.setPaintTicks(true);
        playerSpeedSlider.setSnapToTicks(true);
        playerSpeedSlider.setValue(25);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(playerSpeedSlider, gbc);
        updatesSlider = new JSlider();
        updatesSlider.setEnabled(false);
        updatesSlider.setMajorTickSpacing(1);
        updatesSlider.setMaximum(3);
        updatesSlider.setMinimum(1);
        updatesSlider.setMinorTickSpacing(1);
        updatesSlider.setPaintLabels(true);
        updatesSlider.setPaintTicks(true);
        updatesSlider.setSnapToTicks(true);
        updatesSlider.setValue(2);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(updatesSlider, gbc);
        tickRateSlider = new JSlider();
        tickRateSlider.setEnabled(false);
        tickRateSlider.setMajorTickSpacing(50);
        tickRateSlider.setMaximum(200);
        tickRateSlider.setMinimum(50);
        tickRateSlider.setMinorTickSpacing(50);
        tickRateSlider.setPaintLabels(true);
        tickRateSlider.setPaintTicks(true);
        tickRateSlider.setSnapToTicks(true);
        tickRateSlider.setValue(100);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 10;
        container.add(tickRateSlider, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.VERTICAL;
        container.add(spacer10, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        container.add(spacer11, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.fill = GridBagConstraints.VERTICAL;
        container.add(spacer12, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.VERTICAL;
        container.add(spacer13, gbc);
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(spacer14, gbc);
        final JLabel label9 = new JLabel();
        Font label9Font = this.$$$getFont$$$(null, Font.BOLD, -1, label9.getFont());
        if (label9Font != null) label9.setFont(label9Font);
        label9.setText("Map");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label9, gbc);
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        container.add(spacer15, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("Game Port");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label10, gbc);
        tfClientPort = new JTextField();
        tfClientPort.setText("32000");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(tfClientPort, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Fullscreen");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(label11, gbc);
        checkFullscreen = new JCheckBox();
        checkFullscreen.setEnabled(true);
        checkFullscreen.setSelected(false);
        checkFullscreen.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(checkFullscreen, gbc);
        cbWindowSize = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("1280x720");
        defaultComboBoxModel2.addElement("800x480");
        cbWindowSize.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(cbWindowSize, gbc);
        lblWindowSize = new JLabel();
        lblWindowSize.setText("Window Size");
        lblWindowSize.setVisible(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        container.add(lblWindowSize, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(mapsComboBox, gbc);
        btnStartGame = new JButton();
        btnStartGame.setText("Start Game");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 11;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(btnStartGame, gbc);
        label2.setLabelFor(tfClientIP);
        label10.setLabelFor(tfClientPort);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {resultName = currentFont.getName();} else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {resultName = fontName;} else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return container; }
}
