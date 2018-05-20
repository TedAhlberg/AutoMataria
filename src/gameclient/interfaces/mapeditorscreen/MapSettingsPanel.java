package gameclient.interfaces.mapeditorscreen;

import common.GameMap;
import common.Utility;
import gameclient.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Johannes Blüml
 */
class MapSettingsPanel extends JComponent {
    private MapEditorScreen mapEditorScreen;
    private JTextField mapNameTextField;
    private JComboBox<String> backgroundImageComboBox, gridSizeComboBox, musicTrackComboBox;
    private JComboBox<Integer> playersComboBox;
    private JComboBox<Double> playerSpeedComboBox;

    MapSettingsPanel(MapEditorScreen mapEditorScreen) {
        this.mapEditorScreen = mapEditorScreen;
        createComponents();
        createLayout();
    }

    private void createComponents() {
        mapNameTextField = new JTextField();
        mapNameTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                mapEditorScreen.setMapName(mapNameTextField.getText());
            }
        });

        backgroundImageComboBox = new JComboBox<>(Resources.getBackgroundImageList());
        backgroundImageComboBox.addActionListener(e ->
                mapEditorScreen.setMapBackgroundImage((String) backgroundImageComboBox.getSelectedItem()));

        musicTrackComboBox = new JComboBox<>(Resources.getMusicList());
        musicTrackComboBox.addActionListener(e ->
                mapEditorScreen.setMapMusicTrack((String) musicTrackComboBox.getSelectedItem()));

        gridSizeComboBox = new JComboBox<>(new String[]{"Large", "Normal", "Small"});
        gridSizeComboBox.setSelectedItem("Normal");
        gridSizeComboBox.addActionListener(e ->
                mapEditorScreen.setMapGrid((String) gridSizeComboBox.getSelectedItem()));

        playersComboBox = new JComboBox<>(new Integer[]{
                2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16
        });
        playersComboBox.setSelectedItem(5);
        playersComboBox.addActionListener(e ->
                mapEditorScreen.setMapPlayers((int) playersComboBox.getSelectedItem()));

        playerSpeedComboBox = new JComboBox<>(new Double[]{
                0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0
        });
        playerSpeedComboBox.setSelectedItem(1.0);
        playerSpeedComboBox.addActionListener(e ->
                mapEditorScreen.setMapPlayerSpeedMultiplier((double) playerSpeedComboBox.getSelectedItem()));
    }

    private void createLayout() {
        setLayout(new GridBagLayout());

        addTableRow("MAP NAME", mapNameTextField);
        addTableRow("GRID SIZE", gridSizeComboBox);
        addTableRow("PLAYERS", playersComboBox);
        addTableRow("SPEED MULTIPLIER", playerSpeedComboBox);
        addTableRow("BACKGROUND", backgroundImageComboBox);
        addTableRow("MUSIC TRACK", musicTrackComboBox);
        addSpacer();
    }

    private void addSpacer() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(Box.createVerticalStrut(0), gbc);
    }

    private void addTableRow(String labelText, JComponent component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.ipady = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setLabelFor(component);

        gbc.gridx = 0;
        add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        add(component, gbc);
    }

    void setCurrentMap(GameMap map) {
        mapNameTextField.setText(map.getName());
        gridSizeComboBox.setSelectedItem(Utility.getGridSizeName(map.getGrid()));
        playersComboBox.setSelectedItem(map.getPlayers());
        playerSpeedComboBox.setSelectedItem(map.getPlayerSpeedMultiplier());
        backgroundImageComboBox.setSelectedItem(map.getBackground());
        musicTrackComboBox.setSelectedItem(map.getMusicTrack());
    }
}
