package gameclient.interfaces.hostserverscreen;

import common.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

/**
 * @author Johannes Blüml
 */
public class MapPoolPanel extends JPanel {
    private HashSet<String> selectedMaps = new HashSet<>();
    private HashSet<JCheckBox> checkBoxes = new HashSet<>();

    public MapPoolPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        loadMaps();
    }

    public void loadMaps() {
        removeAll();
        String[] mapList = Maps.getInstance().getMapList();
        for (String map : mapList) {
            addMap(map);
        }
    }

    public String[] getSelectedMaps() {
        return selectedMaps.toArray(new String[0]);
    }

    private void addMap(String map) {
        GameMap gameMap = Maps.getInstance().get(map);

        JCheckBox checkBox = new JCheckBox();
        checkBox.addChangeListener(e -> {
            if (checkBox.isSelected()) {
                selectedMaps.add(map);
            } else {
                selectedMaps.remove(map);
            }
        });
        checkBox.setText(map.toUpperCase());
        checkBox.setHorizontalAlignment(SwingConstants.LEFT);
        checkBox.setSelected(true);
        checkBoxes.add(checkBox);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        add(checkBox, gbc);
        gbc.gridx = 1;
        add(new JLabel(gameMap.getPlayers() + " PLAYERS"), gbc);
        gbc.gridx = 2;
        add(new JLabel(Utility.getGridSizeName(gameMap.getGrid()).toUpperCase() + " GRIDS"), gbc);
    }

    public void setEnabled(boolean enabled) {
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setEnabled(enabled);
        }
    }
}
