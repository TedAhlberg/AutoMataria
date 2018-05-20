package gameclient.interfaces.mapeditorscreen;

import gameclient.interfaces.AMButton;

import javax.swing.*;
import java.awt.*;

/**
 * @author Johannes Blüml
 */
public class MapTopBarPanel extends JComponent {
    private MapEditorScreen mapEditorScreen;
    private JLabel mapName;
    private AMButton saveMapButton, resetButton, exitButton, helpButton;

    MapTopBarPanel(MapEditorScreen mapEditorScreen) {
        this.mapEditorScreen = mapEditorScreen;

        mapName = new JLabel("SELECTED MAP: -");
        saveMapButton = new AMButton("SAVE MAP");
        saveMapButton.addActionListener(e -> mapEditorScreen.saveMap());
        resetButton = new AMButton("RESET MAP");
        resetButton.addActionListener(e -> mapEditorScreen.newMap());
        exitButton = new AMButton("BACK TO MENU");
        exitButton.addActionListener(e -> mapEditorScreen.exit());
        helpButton = new AMButton("HELP");
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "DRAG PICKUPS TO MOVE THEM\n\n" +
                            "DRAG FROM A WALL TO EXTEND THAT WALL\n\n" +
                            "CLICK ON A PICKUP OR WALL TO CHANGE ITS SETTINGS\n\n" +
                            "RIGHT-CLICK ON AN EMPTY GRID TO ADD A WALL OR PICKUP\n\n" +
                            "RIGHT-CLICK (OR RIGHT-CLICK AND DRAG) TO REMOVE PARTS OF THE SELECTED WALL");
        });

        createLayout();
    }

    private void createLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(saveMapButton, gbc);
        add(resetButton, gbc);
        add(helpButton, gbc);
        gbc.anchor = GridBagConstraints.EAST;
        add(exitButton, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(mapName, gbc);
    }

    void setMapName(String name) {
        mapName.setText("SELECTED MAP: " + name);
    }
}
